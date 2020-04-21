package com.yibao.hanlp.utis;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.yibao.hanlp.hanlpenum.HanLpNatureTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 合并基础分词工具类
 *
 * @author liuwenyi
 * @date 2019/11/26
 */
@Slf4j
public final class BasicsTokenUtil {

    private BasicsTokenUtil() {
    }

    /**
     * 过滤「1. 2、」之类的序号
     */
    private static final List<String> FILTER_NUMERICAL_ORDER_PUNCTUATION_LIST = Lists.newArrayList(".", "、");

    /**
     * 手术方式中的标点符号
     */
    private static final List<String> PATHOLOGY_PUNCTUATION_LIST = Lists.newArrayList("(", ")+", ")",
            "+", "）", "（", "）+", "、");

    /**
     * 特殊标点符号，用来拼接或者跳出
     */
    private static final List<String> PUNCTUATION_LIST = Lists.newArrayList("。", "，", "/");

    private static final List<String> GREATER_LESS_LIST = Lists.newArrayList(">", "<");

    private static final List<String> TRANSVERSE_LINE_LIST = Lists.newArrayList("—", "-", "一", "~");

    /**
     * 疾病标签
     */
    private static final List<Nature> DIS_LIST = Lists.newArrayList(
            HanLpNatureTypeEnum.SYM.getNature(),
            HanLpNatureTypeEnum.NHD.getNature());

    /**
     * 方位词
     */
    private static final List<String> LOCATION_WORD_LIST = Lists.newArrayList("单侧");

    /**
     * 治疗方式中的关键字
     */
    private static final List<Nature> CURE_LIST = Lists.newArrayList(
            HanLpNatureTypeEnum.CURE.getNature(),
            HanLpNatureTypeEnum.M.getNature(),
            HanLpNatureTypeEnum.F.getNature(),
            HanLpNatureTypeEnum.W.getNature()
    );

    private static final List<Nature> DATE_LIST = Lists.newArrayList(
            HanLpNatureTypeEnum.T.getNature(),
            HanLpNatureTypeEnum.M.getNature()
    );

    /**
     * 合并实体
     *
     * @param terms List
     * @return List
     */
    public static List<Term> merge(List<Term> terms) {
        List<Term> termList = handleTerm(terms);
        List<Term> newTermList = Lists.newArrayList();
        int len = termList.size();
        int flag;
        for (int i = 0; i < len; i++) {
            Term term = termList.get(i);
            flag = mergeCure(termList, i, newTermList);
            if (flag != i) {
                i = flag;
                continue;
            }
            flag = mergeUnit(termList, i, newTermList);
            if (flag != i) {
                i = flag;
                continue;
            }
            flag = mergeNumber(termList, i, newTermList);
            if (flag != i) {
                i = flag;
                continue;
            }
            if (PATHOLOGY_PUNCTUATION_LIST.contains(term.word)) {
                continue;
            }
            newTermList.add(term);
        }
        List<Term> secondTermList = Lists.newArrayList();
        int secondFlag;
        for (int i = 0; i < newTermList.size(); i++) {
            secondFlag = mergeDis(newTermList, i, secondTermList);
            if (secondFlag != i) {
                i = secondFlag;
                continue;
            }
            secondFlag = mergeDate(newTermList, i, secondTermList);
            if (secondFlag != i) {
                i = secondFlag;
                continue;
            }
            secondFlag = handleRange(newTermList, i, secondTermList);
            if (secondFlag != i) {
                i = secondFlag;
                continue;
            }
            secondTermList.add(newTermList.get(i));
        }
        return secondTermList;
    }

    /**
     * 合并日期
     *
     * @param termList    List
     * @param i           int
     * @param newTermList List
     * @return int
     */
    private static int mergeDate(List<Term> termList, int i, List<Term> newTermList) {
        int next = i + 1;
        if (next >= termList.size()
                || !termList.get(i).nature.equals(HanLpNatureTypeEnum.T.getNature())
                || !DATE_LIST.contains(termList.get(next).nature)) {
            return i;
        }
        StringBuilder dateTime = new StringBuilder();
        while (i < termList.size()
                && DATE_LIST.contains(termList.get(i).nature)) {
            if (termList.get(i).nature.equals(HanLpNatureTypeEnum.T.getNature())) {
                dateTime.append(termList.get(i).word);
            }
            i++;
        }
        if (!dateTime.toString().isEmpty()) {
            newTermList.add(new Term(dateTime.toString(), HanLpNatureTypeEnum.T.getNature()));
            i--;
            return i;
        }
        return i;
    }

    /**
     * 合并方位介词和疾病
     *
     * @param termList    List
     * @param i           int
     * @param newTermList List
     * @return int
     */
    private static int mergeDis(List<Term> termList, int i, List<Term> newTermList) {
        Term term = termList.get(i);
        int next = i + 1;
        if (next >= termList.size()) {
            return i;
        }
        Term nextTerm = termList.get(next);
        boolean isRightPreposition = DIS_LIST.contains(term.nature)
                && (nextTerm.nature.equals(HanLpNatureTypeEnum.F.getNature())
                || LOCATION_WORD_LIST.contains(nextTerm.word));
        boolean isLeftPreposition = (term.nature.equals(HanLpNatureTypeEnum.F.getNature())
                || LOCATION_WORD_LIST.contains(term.word))
                && DIS_LIST.contains(termList.get(next).nature);
        if (!isLeftPreposition && !isRightPreposition) {
            return i;
        }
        StringBuilder sb = new StringBuilder();
        // 合并「甲状腺癌，单侧」or「甲状腺癌，左」方位介词在右边
        if (isRightPreposition) {
            sb.append(term.word).append(nextTerm.word);
            newTermList.add(new Term(sb.toString(), term.nature));
            return next;
        }
        // 合并「单侧，甲状腺癌」or「左，甲状腺癌」方位介词在左边
        sb.append(term.word).append(nextTerm.word);
        newTermList.add(new Term(sb.toString(), nextTerm.nature));
        return next;
    }

    /**
     * 合并治疗方式「在全麻下行左甲状腺癌根治术单侧峡部及右侧近峡部切除+淋巴显影+甲状旁腺移植术」
     *
     * @param termList    List
     * @param i           int
     * @param newTermList List
     * @return int
     */
    private static int mergeCure(List<Term> termList, int i, List<Term> newTermList) {
        Term term = termList.get(i);
        if (i + 1 >= termList.size()
                || !term.nature.equals(HanLpNatureTypeEnum.CURE.getNature())) {
            return i;
        }
        StringBuilder cureValue = new StringBuilder();
        int count = 0;
        while ((CURE_LIST.contains(termList.get(i).nature)
                || PATHOLOGY_PUNCTUATION_LIST.contains(termList.get(i).word))) {
            cureValue.append(termList.get(i).word);
            i++;
            count++;
        }
        if (!cureValue.toString().isEmpty() && count > 1) {
            newTermList.add(new Term(cureValue.toString(), HanLpNatureTypeEnum.CURE_.getNature()));
            return i - 1;
        }
        return i - 1;
    }

    /**
     * 合并单位
     *
     * @param termList    List
     * @param i           int
     * @param newTermList List
     * @return int
     */
    private static int mergeUnit(List<Term> termList, int i, List<Term> newTermList) {
        int third = i + 2;
        if (third >= termList.size()) {
            return i;
        }
        if (termList.get(i).nature.equals(Nature.nx)
                && termList.get(i + 1).nature.equals(Nature.w)
                && termList.get(i + 2).nature.equals(Nature.nx)) {
            String newWord = termList.get(i).word + termList.get(i + 1).word + termList.get(i + 2).word;
            newTermList.add(new Term(newWord, HanLpNatureTypeEnum.UNIT.getNature()));
            return third;
        }
        return i;
    }

    private static int mergeNumber(List<Term> termList, int i, List<Term> newTermList) {
        Term term = termList.get(i);
        if (!termList.get(i).nature.equals(Nature.m)) {
            return i;
        }
        int j = i + 2;
        if (j < termList.size()
                && termList.get(i + 1).nature.equals(Nature.nx)
                && isContainsTransverseLine(termList.get(i + 1))
                && termList.get(i + 2).nature.equals(Nature.m)) {
            String newValue = term.word.trim() + termList.get(i + 1).word.trim() + termList.get(i + 2).word.trim();
            newTermList.add(new Term(newValue, HanLpNatureTypeEnum.MQ.getNature()));
            return j;
        }
        if (i + 1 < termList.size()
                && isEndTransverseLine(term.word)
                && termList.get(i + 1).nature.equals(HanLpNatureTypeEnum.M.getNature())) {
            String newValue = term.word.trim() + termList.get(i + 1).word.trim();
            newTermList.add(new Term(newValue, HanLpNatureTypeEnum.MQ.getNature()));
            return i + 1;
        }
        return i;
    }

    private static boolean isEndTransverseLine(String word) {
        for (String t : TRANSVERSE_LINE_LIST) {
            if (word.endsWith(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理特殊参考范围
     *
     * @param termList    List
     * @param i           int
     * @param newTermList List
     * @return int
     */
    private static int handleRange(List<Term> termList, int i, List<Term> newTermList) {
        if (!termList.get(i).nature.equals(HanLpNatureTypeEnum.THYROID_FUNCTION.getNature())) {
            return i;
        }
        newTermList.add(termList.get(i));
        i++;
        Term termNum = null;
        while (i < termList.size() && !termList.get(i).nature.equals(HanLpNatureTypeEnum.THYROID_FUNCTION.getNature())) {
            Term term = termList.get(i);
            newTermList.add(term);
            if (termNum != null && i + 1 < termList.size()
                    && term.nature.equals(HanLpNatureTypeEnum.GL.getNature())
                    && termList.get(i + 1).nature.equals(HanLpNatureTypeEnum.M.getNature())) {
                newTermList.add(new Term(termList.get(i + 1).word, HanLpNatureTypeEnum.MQ.getNature()));
                return i + 1;
            }
            if (term.nature.equals(HanLpNatureTypeEnum.M.getNature())) {
                termNum = term;
            }
            i++;
        }
        return i - 1;
    }

    /**
     * 处理识别结果
     *
     * @param termList List
     * @return List
     */
    private static List<Term> handleTerm(List<Term> termList) {
        List<Term> terms = Lists.newArrayList();
        for (int i = 0; i < termList.size(); i++) {
            Term term = termList.get(i);
            // 过滤「1. 2、」序号
            if (i + 1 < termList.size()
                    && term.nature.equals(Nature.m)
                    && FILTER_NUMERICAL_ORDER_PUNCTUATION_LIST.contains(termList.get(i + 1).word)) {
                i++;
                continue;
            }
            // 过滤标点符号
            if (term.nature.equals(Nature.w)
                    && !PATHOLOGY_PUNCTUATION_LIST.contains(term.word)
                    && !PUNCTUATION_LIST.contains(term.word)) {
                continue;
            }
            if (i + 1 < termList.size() && GREATER_LESS_LIST.contains(term.word)
                    && termList.get(i + 1).nature.equals(HanLpNatureTypeEnum.M.getNature())) {
                terms.add(new Term(term.word, HanLpNatureTypeEnum.GL.getNature()));
                continue;
            }
            if (term.nature.equals(HanLpNatureTypeEnum.MQ.getNature())
                    && !isContainsTransverseLine(term)) {
                terms.add(new Term(term.word, HanLpNatureTypeEnum.GL_.getNature()));
                continue;
            }
            terms.add(term);
        }
        return terms;
    }

    private static boolean isContainsTransverseLine(Term term) {
        return TRANSVERSE_LINE_LIST.stream()
                .anyMatch(s -> term.word.contains(s));
    }
}
