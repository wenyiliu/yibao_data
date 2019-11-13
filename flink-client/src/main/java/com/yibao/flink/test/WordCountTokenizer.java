package com.yibao.flink.test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * @author liuwenyi
 * @date 2019/11/13
 */
public class WordCountTokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

    public WordCountTokenizer() {
    }

    @Override
    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
        String[] tokens = s.toLowerCase().split("\\W+");
        int len = tokens.length;

        for (int i = 0; i < len; i++) {
            String tmp = tokens[i];
            if (tmp.length() > 0) {
                collector.collect(new Tuple2<>(tmp, 1));
            }
        }
    }
}
