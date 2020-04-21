package com.yibao.hanlp.utis;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Segment;

/**
 * @author liuwenyi
 * @date 2020/04/21
 */
public class HanLpUtil {

    public static final Segment N_SHORT;

    public static final Segment NEW_SEGMENT;

    static {
        // 最短路径分词
        N_SHORT = new NShortSegment()
                // 开启自定义词典功能
                .enableCustomDictionary(true)
                // 开启数字合并公共
                .enableNumberQuantifierRecognize(true)
                // 开启分词的起始位置
                .enableOffset(true);

        // 开启普通分词
        NEW_SEGMENT = HanLP.newSegment()
                // 开启自定义词典功能
                .enableCustomDictionary(true);
    }

    private HanLpUtil() {
        //ignored
    }
}
