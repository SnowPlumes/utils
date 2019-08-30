package me.lv.main;

import me.lv.entity.User;
import me.lv.util.ExcelUtil;
import me.lv.util.SensitivewordFilter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author lv
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        importExcel();
//        exportExcel();
        sensitiveWord();
    }

    public static void importExcel() {
        ExcelUtil.importExcel(new File("D:/123.xls"));
    }

    public static void exportExcel() throws Exception {
        String titleStr = "姓名,年龄";
        String fieldStr = "name,age";
        List<User> list = new LinkedList<>();
        list.add(new User(12, "张三"));
        list.add(new User(16, "李四"));
        ExcelUtil.exportExcel(titleStr, fieldStr, list);
    }

    public static void sensitiveWord() {
        SensitivewordFilter filter = new SensitivewordFilter();
        System.out.println("敏感词的数量：" + filter.sensitiveWordMap.size());
        System.out.println("敏感词:" + filter.sensitiveWordMap);
        String string = "太多的伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
                + "然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
                + "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
        System.out.println("待检测语句字数：" + string.length());
        long beginTime = System.currentTimeMillis();
        Set<String> set = filter.getSensitiveWord(string, 1);
        long endTime = System.currentTimeMillis();
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        System.out.println("总共消耗时间为：" + (endTime - beginTime));
    }
}
