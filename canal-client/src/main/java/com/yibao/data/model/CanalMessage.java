package com.yibao.data.model;

import com.alibaba.otter.canal.protocol.CanalEntry;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author liuwenyi
 * @date 2019/10/31
 */
@Data
@Builder
public class CanalMessage {

    /**
     * 数据库实例名称
     */
    private String schemaName;
    /**
     * 数据库表名称
     */
    private String tableName;

    /**
     * 操作类型
     */
    private CanalEntry.EventType eventType;

    /**
     * ddl
     */
    private boolean isDdl;

    /**
     * 原始列
     */
    private List<ColumnChange> beforeColumnList;

    /**
     * 新生列
     */
    private List<ColumnChange> afterColumnList;

    @Data
    @Builder
    public static class ColumnChange {

        /**
         * 字段名
         */
        private String name;

        /**
         * 是否是主键
         */
        private boolean isKey;

        /**
         * 字段值是否更新
         */
        private boolean updated;

        /**
         * 是否为空
         */
        private boolean isNull;

        /**
         * 字段值
         */
        private String value;
    }
}
