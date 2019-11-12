package com.yibao.data.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuwenyi
 * @date 2019/10/31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ListenPoint {

    String[] schema() default {};

    String[] table() default {};

    CanalEntry.EventType[] eventType() default {};
}
