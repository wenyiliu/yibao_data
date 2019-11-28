package com.yibao.data.handler;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.common.collect.Lists;
import com.yibao.data.annotation.ListenPoint;
import com.yibao.data.listener.ListenerPoint;
import com.yibao.data.model.CanalMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author liuwenyi
 * @date 2019/10/30
 */
public class MessageHandler implements Handler<Message> {

    private final static Logger log = LoggerFactory.getLogger(MessageHandler.class);

    private List<ListenerPoint> annotationList;


    public MessageHandler(List<ListenerPoint> annotationList) {
        this.annotationList = annotationList;
    }

    @Override
    public Boolean doChain(Message message) {
        List<CanalEntry.Entry> entryList = message.getEntries();
        List<CanalEntry.EntryType> ignoreEntryTypes = getIgnoreEntryTypes();
        for (CanalEntry.Entry entry : entryList) {
            if (ignoreEntryTypes.stream().anyMatch(t -> entry.getEntryType() == t)) {
                continue;
            }
            CanalEntry.RowChange rowChange;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                rowChange.getRowDatasList().forEach(rowData -> {
                    CanalMessage build = CanalMessage.builder()
                            .schemaName(entry.getHeader().getSchemaName())
                            .tableName(entry.getHeader().getTableName())
                            .eventType(rowChange.getEventType())
                            .isDdl_(rowChange.getIsDdl())
                            .beforeColumnList(getChangeList(rowData, Boolean.FALSE))
                            .afterColumnList(getChangeList(rowData, Boolean.TRUE))
                            .build();
                    doAnnotation(build);
                });
            } catch (Exception e) {
                throw new CanalClientException("转换错误,数据信息:", entry.toString());
            }

        }
        return true;
    }

    private void doAnnotation(CanalMessage message) {
        if (!CollectionUtils.isEmpty(annotationList)) {
            annotationList.forEach(point -> point.getInvokeMap()
                    .entrySet()
                    .stream()
                    .filter(getAnnotationFilter(message))
                    .forEach(entry -> {
                        Method method = entry.getKey();
                        method.setAccessible(Boolean.TRUE);
                        try {
                            Object[] args = getInvokeArgs(method, message);
                            method.invoke(point.getTarget(), args);
                        } catch (Exception e) {
                            log.error("{}: 委托 canal 监听器发生错误! 错误类:{}, 方法名:{}",
                                    Thread.currentThread().getName(),
                                    point.getTarget().getClass().getName(), method.getName());
                        }
                    })
            );
        }
    }

    private List<CanalEntry.EntryType> getIgnoreEntryTypes() {
        return Arrays.asList(CanalEntry.EntryType.TRANSACTIONBEGIN, CanalEntry.EntryType.TRANSACTIONEND,
                CanalEntry.EntryType.HEARTBEAT);
    }

    private Predicate<Map.Entry<Method, ListenPoint>> getAnnotationFilter(CanalMessage message) {

        //看看数据库实例名是否一样
        Predicate<Map.Entry<Method, ListenPoint>> sf = e -> e.getValue().schema().length == 0
                || Arrays.stream(e.getValue().schema()).anyMatch(s -> s.equals(message.getSchemaName()))
                || message.getSchemaName() == null;

        //看看表名是否一样
        Predicate<Map.Entry<Method, ListenPoint>> tf = e -> e.getValue().table().length == 0
                || Arrays.stream(e.getValue().table()).anyMatch(t -> t.equals(message.getTableName()))
                || message.getTableName() == null;

        //类型一致？
        Predicate<Map.Entry<Method, ListenPoint>> ef = e -> e.getValue().eventType().length == 0
                || Arrays.stream(e.getValue().eventType()).anyMatch(ev -> ev == message.getEventType())
                || message.getEventType() == null;
        return sf.and(tf).and(ef);
    }

    private Object[] getInvokeArgs(Method method, CanalMessage message) {
        return Arrays.stream(method.getParameterTypes())
                .map(p -> p == CanalMessage.class ? message : null)
                .toArray();
    }

    /**
     * 获取改变的列
     *
     * @param rowData  列值的变化
     * @param isChange true 改变后的列 false 改变前的列
     * @return List<CanalMessage.ColumnChange>
     */
    private static List<CanalMessage.ColumnChange> getChangeList(CanalEntry.RowData rowData, boolean isChange) {
        List<CanalMessage.ColumnChange> columnChangeList = Lists.newArrayList();
        if (isChange) {
            columnChangeList.addAll(getColumnList(rowData.getAfterColumnsList()));
        } else {
            columnChangeList.addAll(getColumnList(rowData.getBeforeColumnsList()));
        }
        return columnChangeList;
    }

    private static List<CanalMessage.ColumnChange> getColumnList(List<CanalEntry.Column> columnList) {
        return columnList.stream().map(item -> CanalMessage.ColumnChange.builder()
                .name(item.getName())
                .isKey_(item.getIsKey())
                .isNull_(item.getIsNull())
                .updated(item.getUpdated())
                .value(item.getValue())
                .build())
                .collect(Collectors.toList());
    }
}
