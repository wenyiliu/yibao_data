package com.yibao.data.listener;

import com.yibao.data.annotation.ListenPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuwenyi
 * @date 2019/10/31
 */
public class ListenerPoint {

    private Object target;

    private Map<Method, ListenPoint> invokeMap = new HashMap<>();

    public ListenerPoint(Object target, Method method, ListenPoint point) {
        this.invokeMap.put(method, point);
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public Map<Method, ListenPoint> getInvokeMap() {
        return invokeMap;
    }
}
