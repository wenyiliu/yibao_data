package com.yibao.data.handler;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
public interface Handler<T> {

    /**
     * binlog数据解析
     *
     * @param t T
     * @return Boolean
     */
    Boolean doChain(T t);
}
