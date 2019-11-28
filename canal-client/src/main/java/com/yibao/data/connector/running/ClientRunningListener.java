package com.yibao.data.connector.running;

/**
 * @author liuwenyi
 * @date 2019/11/27
 */
public interface ClientRunningListener {
    /**
     * 触发现在轮到自己做为active
     */
    public void processActiveEnter();

    /**
     * 触发一下当前active模式失败
     */
    public void processActiveExit();
}
