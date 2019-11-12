package com.yibao.data.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.common.collect.Lists;
import com.yibao.data.annotation.CanalEventListener;
import com.yibao.data.annotation.ListenPoint;
import com.yibao.data.config.CanalConfig;
import com.yibao.data.listener.ListenerPoint;
import com.yibao.data.util.BeanUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author liuwenyi
 * @date 2019/10/31
 */
public abstract class AbstractCanalClient implements CanalClient {

    private volatile boolean running;

    private List<ListenerPoint> annotationList = Lists.newArrayList();

    private CanalConfig config;

    private CanalConnector connector;

    AbstractCanalClient(CanalConfig config) {
        this.config = config;
        initListeners();
    }

    @Override
    public void start() {
        setRunning(Boolean.TRUE);
        Map<String, CanalConfig.Instance> instanceMap = getConfig();
        for (Map.Entry<String, CanalConfig.Instance> instanceEntry : instanceMap.entrySet()) {
            process(processInstanceEntry(instanceEntry), instanceEntry);
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
        if (listenerMap != null) {
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
    }

    public List<ListenerPoint> getAnnotationList() {
        return annotationList;
    }

    public CanalConnector getConnector() {
        return connector;
    }

    public void setConnector(CanalConnector connector) {
        this.connector = connector;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
