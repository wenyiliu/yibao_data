package com.yibao.data.handler;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
public interface Handler<T> {

    /**
     * 处理binlog数据
     *
     * @param t T
     * @return Boolean
     */
    Boolean doChain(T t);
}
