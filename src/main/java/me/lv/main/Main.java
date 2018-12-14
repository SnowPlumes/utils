package me.lv.main;

import me.lv.util.ExcelUtil;

import java.io.File;

/**
 * @author lv
 */
public class Main {
    public static void main(String[] args) {
        ExcelUtil.importExcel(new File("D:/123.xls"));
    }
}
