package com.yibao.data.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yibao.data.annotation.CanalEventListener;
import com.yibao.data.annotation.ListenPoint;
import com.yibao.data.config.CanalConfig;
import com.yibao.data.listener.ListenerPoint;
import com.yibao.data.util.BeanUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liuwenyi
 * @date 2019/10/31
 */
public abstract class AbstractCanalClient implements CanalClient {

    private volatile boolean running;

    private List<ListenerPoint> annotationList = Lists.newArrayList();

    private CanalConfig config;

    /**
     * 声明一个线程池
     */
    private ThreadPoolExecutor executor;

    AbstractCanalClient(CanalConfig config) {
        this.config = config;
        executor = new ThreadPoolExecutor(3, 5, 60L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024), new ThreadFactoryBuilder()
                .setNameFormat("canal-client-%d").build(), new ThreadPoolExecutor.AbortPolicy());
        initListeners();
    }

    @Override
    public void start() {
        setRunning(Boolean.TRUE);
        Map<String, CanalConfig.Instance> instanceMap = getConfig();
        for (Map.Entry<String, CanalConfig.Instance> instanceEntry : instanceMap.entrySet()) {
            executor.submit(() -> process(processInstanceEntry(instanceEntry), instanceEntry));
        }
    }

    @Override
    public void stop() {
        setRunning(Boolean.FALSE);
    }

    /**
     * 获取数据
     *
     * @param connector CanalConnector
     * @param instance  Map
     */
    abstract void process(CanalConnector connector, Map.Entry<String, CanalConfig.Instance> instance);

    /**
     * canal连接实例
     *
     * @param instanceEntry Map
     * @return CanalConnector
     */
    public abstract CanalConnector processInstanceEntry(Map.Entry<String, CanalConfig.Instance> instanceEntry);

    private Map<String, CanalConfig.Instance> getConfig() {
        //canal 配置
        CanalConfig config = this.config;
        Map<String, CanalConfig.Instance> instanceMap;
        if (config != null && (instanceMap = config.getInstances()) != null && !instanceMap.isEmpty()) {
            //返回配置实例
            return config.getInstances();
        } else {
            throw new CanalClientException("无法解析 canal 的连接信息，请联系开发人员!");
        }
    }

    private void initListeners() {
        Map<String, Object> listenerMap = BeanUtil.getBeansWithAnnotation(CanalEventListener.class);
        //也放入 map
        if (listenerMap.isEmpty()) {
            return;
        }
        for (Object target : listenerMap.values()) {
            //方法获取
            Method[] methods = target.getClass().getDeclaredMethods();
            if (methods == null || methods.length <= 0) {
                continue;
            }
            for (Method method : methods) {
                ListenPoint l = AnnotatedElementUtils.findMergedAnnotation(method, ListenPoint.class);
                if (l != null) {
                    annotationList.add(new ListenerPoint(target, method, l));
                }
            }
        }
    }

    List<ListenerPoint> getAnnotationList() {
        return annotationList;
    }

    boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }
}
