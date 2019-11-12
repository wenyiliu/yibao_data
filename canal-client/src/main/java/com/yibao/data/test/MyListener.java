package com.yibao.data.test;

import com.yibao.data.annotation.CanalEventListener;
import com.yibao.data.annotation.dml.UpdateListenPoint;
import com.yibao.data.model.CanalMessage;

/**
 * @author liuwenyi
 * @date 2019/11/12
 */
@CanalEventListener
public class MyListener {

    @UpdateListenPoint
    public void update(CanalMessage message) {
        System.out.println(message.toString());
    }
}
