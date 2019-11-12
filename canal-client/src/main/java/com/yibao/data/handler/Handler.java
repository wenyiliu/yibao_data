package com.yibao.data.handler;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
public interface Handler<T> {

    Boolean doChain(T t);
}
