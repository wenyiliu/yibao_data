package com.yibao.hanlp.hanlpenum;

import com.hankcs.hanlp.corpus.tag.Nature;
import lombok.Getter;

/**
 * @author liuwenyi
 * @date 2019/12/10
 */
public enum HanLpNatureTypeEnum {
    /**
     * hanLp词性
     */
    NULL(Nature.create("null"), ""),
    M(Nature.m, "数字类型"),
    KW(Nature.create("kw"), "关键字"),
    NTH(Nature.nth, "医院"),
    T(Nature.t, "时间"),
    F(Nature.f, "方位词"),
    W(Nature.w, "标点"),
    CURE(Nature.create("cure"), "治疗方式"),
    NHD(Nature.nhd, "疾病"),
    CURE_(Nature.create("Cure"), "合并后的治疗方式"),
    SYM(Nature.create("sym"), "症状"),
    FRACTIONAL(Nature.create("fra"), "分数类型"),
    NUMBER(Nature.create("num"), "数字"),
    NUMBER_(Nature.create("Num"), "合并后的数字"),
    PATHOLOGY(Nature.create("pathology"), "病理诊断"),
    THYROID_FUNCTION(Nature.create("tf"), "甲状腺功能"),
    UNIT(Nature.create("unit"), "单位"),
    MQ(Nature.mq, "特殊数字"),
    GL(Nature.create("gl"), "范围值"),
    GL_(Nature.create("Gl"), "特殊范围值"),
    THYROID_PUNCTURE(Nature.create("t_pun"), "甲状腺穿刺"),
    THYROID_PUNCTURE_POSITION(Nature.create("t_pos"), "甲状腺穿刺具体位置"),
    LYMPH_PUNCTURE(Nature.create("l_pun"), "淋巴结穿刺"),
    PUNCTURE_RESULT(Nature.create("pd"), "穿刺结论"),
    PTH(Nature.create("pth"), "甲状旁腺激素"),
    OSTEOCALCIN(Nature.create("ost"), "游离钙"),
    BLOOD_CALCIUM(Nature.create("bca"), "血钙"),
    CALCITONIN(Nature.create("pct"), "降钙素，降钙素原"),
    OTHER(Nature.create("oth"), "其他"),
    OPERATION(Nature.create("opt"), "手术"),
    ;

    @Getter
    private Nature nature;

    @Getter
    private String desc;


    HanLpNatureTypeEnum(Nature nature, String desc) {
        this.nature = nature;
        this.desc = desc;
    }

    public static HanLpNatureTypeEnum getHanLpNature(Nature nature) {
        for (HanLpNatureTypeEnum h : values()) {
            if (nature.equals(h.getNature())) {
                return h;
            }
        }
        return NULL;
    }
}
