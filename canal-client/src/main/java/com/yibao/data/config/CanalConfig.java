package com.yibao.data.config;

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
@Primary
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConfigurationProperties(prefix = "canal.client")
public class CanalConfig {

    private Map<String, Instance> instances = new LinkedHashMap<>();

    public Map<String, Instance> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, Instance> instances) {
        this.instances = instances;
    }

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

        public boolean isClusterEnabled() {
            return clusterEnabled;
        }

        public void setClusterEnabled(boolean clusterEnabled) {
            this.clusterEnabled = clusterEnabled;
        }

        public Set<String> getZookeeperAddress() {
            return zookeeperAddress;
        }

        public void setZookeeperAddress(Set<String> zookeeperAddress) {
            this.zookeeperAddress = zookeeperAddress;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public long getAcquireInterval() {
            return acquireInterval;
        }

        public void setAcquireInterval(long acquireInterval) {
            this.acquireInterval = acquireInterval;
        }

        public long getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(long waitTime) {
            this.waitTime = waitTime;
        }

        public boolean isKafkaEnable() {
            return kafkaEnable;
        }

        public void setKafkaEnable(boolean kafkaEnable) {
            this.kafkaEnable = kafkaEnable;
        }

        public String getServers() {
            return servers;
        }

        public void setServers(String servers) {
            this.servers = servers;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public Integer getPartition() {
            return partition;
        }

        public void setPartition(Integer partition) {
            this.partition = partition;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }
}


