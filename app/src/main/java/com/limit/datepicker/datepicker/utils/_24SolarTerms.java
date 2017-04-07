package com.limit.datepicker.datepicker.utils;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:518ad-ccn date:Dec 13, 2011
 * describe：24节气
 * 注：程序中使用到的计算节气公式、节气世纪常量等相关信息参照http://www.360doc.com/content/11/0106/22/5281066_84591519.shtml，
 * 程序的运行得出的节气结果绝大多数是正确的，有少数部份是有误差的
 */
public class _24SolarTerms {
    private static final double D = 0.2422;
    private final static Map<SolarTermsEnum, Integer[]> INCREASE_OFFSET_MAP = new HashMap();//+1偏移
    private final static Map<SolarTermsEnum, Integer[]> DECREASE_OFFSET_MAP = new HashMap();//-1偏移

    /**
     * 24节气
     **/
    public enum SolarTermsEnum {
        LICHUN,//--立春
        YUSHUI,//--雨水
        JINGZHE,//--惊蛰
        CHUNFEN,//春分
        QINGMING,//清明
        GUYU,//谷雨
        LIXIA,//立夏
        XIAOMAN,//小满
        MANGZHONG,//芒种
        XIAZHI,//夏至
        XIAOSHU,//小暑
        DASHU,//大暑
        LIQIU,//立秋
        CHUSHU,//处暑
        BAILU,//白露
        QIUFEN,//秋分
        HANLU,//寒露
        SHUANGJIANG,//霜降
        LIDONG,//立冬
        XIAOXUE,//小雪
        DAXUE,//大雪
        DONGZHI,//冬至
        XIAOHAN,//小寒
        DAHAN;//大寒

        public final static String[] solarTerm = {
                "立春", "雨水", "惊蛰", "春分", "清明", "谷雨",
                "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
                "立秋", "处暑", "白露", "秋分", "寒露", "霜降",
                "立冬", "小雪", "大雪", "冬至", "小寒", "大寒"
        };

        public String getName(){
            return solarTerm[ordinal()];
        }
    }

    static {
        DECREASE_OFFSET_MAP.put(SolarTermsEnum.YUSHUI, new Integer[]{2026});//雨水
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.CHUNFEN, new Integer[]{2084});//春分
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.XIAOMAN, new Integer[]{2008});//小满
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.MANGZHONG, new Integer[]{1902});//芒种
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.XIAZHI, new Integer[]{1928});//夏至
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.XIAOSHU, new Integer[]{1925, 2016});//小暑
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.DASHU, new Integer[]{1922});//大暑
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.LIQIU, new Integer[]{2002});//立秋
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.BAILU, new Integer[]{1927});//白露
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.QIUFEN, new Integer[]{1942});//秋分
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.SHUANGJIANG, new Integer[]{2089});//霜降
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.LIDONG, new Integer[]{2089});//立冬
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.XIAOXUE, new Integer[]{1978});//小雪
        INCREASE_OFFSET_MAP.put(SolarTermsEnum.DAXUE, new Integer[]{1954});//大雪
        DECREASE_OFFSET_MAP.put(SolarTermsEnum.DONGZHI, new Integer[]{1918, 2021});//冬至

        INCREASE_OFFSET_MAP.put(SolarTermsEnum.XIAOHAN, new Integer[]{1982});//小寒
        DECREASE_OFFSET_MAP.put(SolarTermsEnum.XIAOHAN, new Integer[]{2019});//小寒

        INCREASE_OFFSET_MAP.put(SolarTermsEnum.DAHAN, new Integer[]{2082});//大寒
    }

    //定义一个二维数组，第一维数组存储的是20世纪的节气C值，第二维数组存储的是21世纪的节气C值,0到23个，依次代表立春、雨水...大寒节气的C值
    private static final double[][] CENTURY_ARRAY =
            {{4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.888, 6.318, 21.86, 6.5, 22.2, 7.928, 23.65, 8.35,
                    23.95, 8.44, 23.822, 9.098, 24.218, 8.218, 23.08, 7.9, 22.6, 6.11, 20.84}
                    , {3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.04, 5.678, 21.37, 7.108, 22.83,
                    7.5, 23.13, 7.646, 23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94, 5.4055, 20.12}};

    public static int getSolarTermNum(int year, @NonNull String name) {
        name = name.trim().toUpperCase();
        return getSolarTermNum(year, SolarTermsEnum.valueOf(name));
    }

    /**
     * @param year 年份
     * @param solarTerm 节气
     * @return 返回节气是相应月份的第几天
     */
    public static int getSolarTermNum(int year, SolarTermsEnum solarTerm) {
        double centuryValue = 0;//节气的世纪值，每个节气的每个世纪值都不同
        int ordinal = solarTerm.ordinal();

        int centuryIndex = -1;
        if (year >= 1901 && year <= 2000) {//20世纪
            centuryIndex = 0;
        } else if (year >= 2001 && year <= 2100) {//21世纪
            centuryIndex = 1;
        } else {
            throw new RuntimeException("不支持此年份：" + year + "，目前只支持1901年到2100年的时间范围");
        }
        centuryValue = CENTURY_ARRAY[centuryIndex][ordinal];
        int dateNum = 0;
        /**
         * 计算 num =[Y*D+C]-L这是传说中的寿星通用公式
         * 公式解读：年数的后2位乘0.2422加C(即：centuryValue)取整数后，减闰年数
         */
        int y = year % 100;//步骤1:取年分的后两位数
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {//闰年
            if (ordinal == SolarTermsEnum.XIAOHAN.ordinal() || ordinal == SolarTermsEnum.DAHAN.ordinal()
                    || ordinal == SolarTermsEnum.LICHUN.ordinal() || ordinal == SolarTermsEnum.YUSHUI.ordinal()) {
                //注意：凡闰年3月1日前闰年数要减一，
                //即：L=[(Y-1)/4],因为小寒、大寒、立春、雨水这两个节气都小于3月1日,所以 y = y-1
                y = y - 1;//步骤2
            }
        }
        dateNum = (int) (y * D + centuryValue) - (y / 4);//步骤3，使用公式[Y*D+C]-L计算
        dateNum += specialYearOffset(year, solarTerm);//步骤4，加上特殊的年分的节气偏移量
        return dateNum;
    }

    /**
     * 特例,特殊的年分的节气偏移量,由于公式并不完善，所以算出的个别节气的第几天数并不准确，在此返回其偏移量
     *
     * @param year 年份
     * @param term 节气名称
     * @return 返回其偏移量
     */
    public static int specialYearOffset(int year, SolarTermsEnum term) {
        int offset = 0;
        offset += getOffset(DECREASE_OFFSET_MAP, year, term, -1);
        offset += getOffset(INCREASE_OFFSET_MAP, year, term, 1);

        return offset;
    }

    public static int getOffset(Map<SolarTermsEnum, Integer[]> map, int year, SolarTermsEnum term, int offset) {
        int off = 0;
        Integer[] years = map.get(term);
        if (null != years) {
            for (int i : years) {
                if (i == year) {
                    off = offset;
                    break;
                }
            }
        }
        return off;
    }

    public static String solarTermToString(int year) {
        StringBuffer sb = new StringBuffer();
        sb.append("---").append(year);
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {//闰年
            sb.append(" 闰年");
        } else {
            sb.append(" 平年");
        }

        sb.append("\n")
                .append("立春：2月").append(getSolarTermNum(year, SolarTermsEnum.LICHUN.name()))
                .append("日,雨水：2月").append(getSolarTermNum(year, SolarTermsEnum.YUSHUI.name()))
                .append("日,惊蛰:3月").append(getSolarTermNum(year, SolarTermsEnum.JINGZHE.name()))
                .append("日,春分:3月").append(getSolarTermNum(year, SolarTermsEnum.CHUNFEN.name()))
                .append("日,清明:4月").append(getSolarTermNum(year, SolarTermsEnum.QINGMING.name()))
                .append("日,谷雨:4月").append(getSolarTermNum(year, SolarTermsEnum.GUYU.name()))
                .append("日,立夏:5月").append(getSolarTermNum(year, SolarTermsEnum.LIXIA.name()))
                .append("日,小满:5月").append(getSolarTermNum(year, SolarTermsEnum.XIAOMAN.name()))
                .append("日,芒种:6月").append(getSolarTermNum(year, SolarTermsEnum.MANGZHONG.name()))
                .append("日,夏至:6月").append(getSolarTermNum(year, SolarTermsEnum.XIAZHI.name()))
                .append("日,小暑:7月").append(getSolarTermNum(year, SolarTermsEnum.XIAOSHU.name()))
                .append("日,大暑:7月").append(getSolarTermNum(year, SolarTermsEnum.DASHU.name()))
                .append("日,\n立秋:8月").append(getSolarTermNum(year, SolarTermsEnum.LIQIU.name()))
                .append("日,处暑:8月").append(getSolarTermNum(year, SolarTermsEnum.CHUSHU.name()))
                .append("日,白露:9月").append(getSolarTermNum(year, SolarTermsEnum.BAILU.name()))
                .append("日,秋分:9月").append(getSolarTermNum(year, SolarTermsEnum.QIUFEN.name()))
                .append("日,寒露:10月").append(getSolarTermNum(year, SolarTermsEnum.HANLU.name()))
                .append("日,霜降:10月").append(getSolarTermNum(year, SolarTermsEnum.SHUANGJIANG.name()))
                .append("日,立冬:11月").append(getSolarTermNum(year, SolarTermsEnum.LIDONG.name()))
                .append("日,小雪:11月").append(getSolarTermNum(year, SolarTermsEnum.XIAOXUE.name()))
                .append("日,大雪:12月").append(getSolarTermNum(year, SolarTermsEnum.DAXUE.name()))
                .append("日,冬至:12月").append(getSolarTermNum(year, SolarTermsEnum.DONGZHI.name()))
                .append("日,小寒:1月").append(getSolarTermNum(year, SolarTermsEnum.XIAOHAN.name()))
                .append("日,大寒:1月").append(getSolarTermNum(year, SolarTermsEnum.DAHAN.name()));

        return sb.toString();
    }

    public static void main(String[] args) {
        for (int year = 1901; year < 2050; year++) {
            System.out.println(solarTermToString(year));
        }
    }
}

