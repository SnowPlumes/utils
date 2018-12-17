package me.lv.main;

import me.lv.entity.User;
import me.lv.util.ExcelUtil;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lv
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        importExcel();
        exportExcel();
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
}
