package com.yibao.data.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.yibao.data.config.CanalConfig;
import com.yibao.data.handler.MessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
public class SimpleCanalClient extends AbstractCanalClient {

    private final static Logger log = LoggerFactory.getLogger(SimpleCanalClient.class);

    private MessageHandler messageHandler;

    public SimpleCanalClient(CanalConfig config) {
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
    public void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> instance) {
        long interval = instance.getValue().getAcquireInterval();
        int retryCount = instance.getValue().getRetryCount();
        while (isRunning()) {
            try {
                Message message = connector.getWithoutAck(instance.getValue().getBatchSize(),
                        instance.getValue().getWaitTime(), TimeUnit.MICROSECONDS);
                long batchId = message.getId();
                if (batchId == -1 || message.getEntries().size() == 0) {
                    Thread.sleep(interval);
                } else {
                    messageHandler.doChain(message);
                }
                connector.ack(batchId);
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
                    stop();
                    log.error("retryCount={},客户端已经停止", retryCount);
                }
            }
        }
        stop();
        log.info("canal客户端停止");
    }

    @Override
    public CanalConnector processInstanceEntry(Map.Entry<String, CanalConfig.Instance> instanceEntry) {
        CanalConfig.Instance instance = instanceEntry.getValue();
        //声明连接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(instance.getHost(),
                instance.getPort()), instanceEntry.getKey(), instance.getUserName(), instance.getPassword());
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
