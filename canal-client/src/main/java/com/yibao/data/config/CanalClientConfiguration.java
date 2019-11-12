package com.yibao.data.config;

import com.yibao.data.client.CanalClient;
import com.yibao.data.client.KafkaCanalClient;
import com.yibao.data.client.SimpleCanalClient;
import com.yibao.data.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
public class CanalClientConfiguration {

    private final static Logger log = LoggerFactory.getLogger(CanalClientConfiguration.class);

    @Autowired
    private CanalConfig config;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public BeanUtil beanUtil() {
        return new BeanUtil();
    }

    @Bean
    private CanalClient canalClient() {
        log.info("正在尝试连接 canal 客户端....");
        //连接 canal 客户端
        CanalClient client;
        if (getValue()) {
            client = new KafkaCanalClient(config);
            log.info("正在尝试开启 kafka canal 客户端....");
        } else {
            client = new SimpleCanalClient(config);
            log.info("正在尝试开启 canal 客户端....");
        }
        //开启 canal 客户端
        client.start();
        //返回结果
        return client;
    }

    private static Boolean getValue() {
        CanalConfig.Instance instance = new CanalConfig.Instance();
        return true;
    }
}
