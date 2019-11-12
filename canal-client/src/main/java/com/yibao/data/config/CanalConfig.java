package com.yibao.data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
@Data
@Primary
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "canal.client")
public class CanalConfig {

    private Map<String, Instance> instances = new LinkedHashMap<>();

    @Data
    public static class Instance {

        /**
         * 是否是集群模式
         */
        private boolean clusterEnabled;

        /**
         * zookeeper 地址
         */
        private Set<String> zookeeperAddress = new LinkedHashSet<>();

        /**
         * canal 服务器地址，默认是本地的环回地址
         */
        private String host = "127.1.1.1";

        /**
         * canal 服务设置的端口，默认 11111
         */
        private int port = 11111;

        /**
         * 集群 设置的用户名
         */
        private String userName = "";

        /**
         * 集群 设置的密码
         */
        private String password = "";

        /**
         * 批量从 canal 服务器获取数据的最多数目
         */
        private int batchSize = 1000;

        /**
         * 是否有过滤规则
         */
        private String filter;

        /**
         * 当错误发生时，重试次数
         */
        private int retryCount = 5;

        /**
         * 信息捕获心跳时间
         */
        private long acquireInterval = 1000;

        /**
         * 等待时间
         */
        private long waitTime = 500;

        /**
         * 是否使用kafka
         */
        private boolean kafkaEnable;
        /**
         * servers
         */
        private String servers;

        /**
         * topic
         */
        private String topic;

        /**
         * 分区
         */
        private Integer partition;

        /**
         * 分组
         */
        private String groupId;
    }
}


