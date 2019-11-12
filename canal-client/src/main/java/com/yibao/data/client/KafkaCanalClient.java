package com.yibao.data.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.kafka.KafkaCanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.yibao.data.config.CanalConfig;
import com.yibao.data.handler.MessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
public class KafkaCanalClient extends AbstractCanalClient {

    private final static Logger log = LoggerFactory.getLogger(KafkaCanalClient.class);

    private MessageHandler messageHandler;

    public KafkaCanalClient(CanalConfig config) {
        super(config);
        this.messageHandler = new MessageHandler(getAnnotationList());
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> config) {
        KafkaCanalConnector kafkaCanalConnector = (KafkaCanalConnector) connector;
        CanalConfig.Instance instance = config.getValue();
        int retryCount = instance.getRetryCount();
        long interval = instance.getAcquireInterval();
        while (isRunning()) {
            try {
                List<Message> messages = kafkaCanalConnector.getListWithoutAck(instance.getWaitTime(), TimeUnit.MILLISECONDS);
                if (messages == null) {
                    continue;
                }
                for (Message message : messages) {
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {
                        Thread.sleep(instance.getAcquireInterval());
                    } else {
                        messageHandler.doChain(message);
                    }
                }
                kafkaCanalConnector.ack();
            } catch (CanalClientException e) {
                retryCount--;
                log.error("发生错误:{},正在进行第{}次重试", e, retryCount);
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    retryCount = 0;
                }

            } catch (InterruptedException e) {
                retryCount = 0;
                connector.rollback();
            } finally {
                if (retryCount <= 0) {
                    kafkaCanalConnector.unsubscribe();
                    kafkaCanalConnector.disconnect();
                    stop();
                    log.error("retryCount={},kafka canal客户端已经停止", retryCount);
                }
            }
        }
        kafkaCanalConnector.unsubscribe();
        kafkaCanalConnector.disconnect();
        stop();
        log.info("kafka canal 客户端关闭");
    }

    @Override
    public CanalConnector processInstanceEntry(Map.Entry<String, CanalConfig.Instance> instanceEntry) {
        CanalConfig.Instance instance = instanceEntry.getValue();
        KafkaCanalConnector connector = new KafkaCanalConnector(instance.getServers(), instance.getTopic(),
                instance.getPartition(), instance.getGroupId(), instance.getBatchSize(), Boolean.TRUE);
        //canal 连接
        connector.connect();
        if (!StringUtils.isEmpty(instance.getFilter())) {
            //canal 连接订阅，包含过滤规则
            connector.subscribe(instance.getFilter());
        } else {
            //canal 连接订阅，无过滤规则
            connector.subscribe();
        }
        //canal 连接反转
        connector.rollback();
        //返回 canal 连接
        return connector;
    }
}
