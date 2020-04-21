package com.yibao.hanlp.utis;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author liuwenyi
 * @date 2019/12/05
 */
public final class SimilarityUtil {

    private SimilarityUtil() {
        // ignored
    }

    /**
     * 狭义Jaccard
     *
     * @param original 原始文本
     * @param target   目标文本
     * @return double
     */
    public static double jaccardNarrow(String original, String target) {
        if (StringUtils.isBlank(target) || StringUtils.isBlank(original)) {
            return 0;
        }
        Set<Character> targetSet = Sets.newHashSet();
        Set<Character> originalSet = Sets.newHashSet();
        for (int i = 0; i < target.length(); i++) {
            targetSet.add(target.charAt(i));
        }
        for (int j = 0; j < original.length(); j++) {
            originalSet.add(original.charAt(j));
        }
        //相同元素个数（交集）
        double commonNum = 0;
        for (Character ch1 : targetSet) {
            for (Character ch2 : originalSet) {
                if (ch1.equals(ch2)) {
                    commonNum++;
                }
            }
        }
        return commonNum / (targetSet.size() + originalSet.size() - commonNum);
    }
}
