package me.lv.utils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lzw on 2018/4/17.
 */
public class ExcelUtil {

    //默认单元格内容为数字时格式
    private static DecimalFormat decimalFormat = new DecimalFormat("0");
    // 默认单元格格式化日期字符串
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final long MIN_FILE_SIZE = 40960L;

    public static DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public static void setDecimalFormat(DecimalFormat decimalFormat) {
        ExcelUtil.decimalFormat = decimalFormat;
    }

    public static SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public static void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        ExcelUtil.simpleDateFormat = simpleDateFormat;
    }

    public static List<Map<String, String>> importExcel(File file) throws Exception {
        //装载流
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(new FileInputStream(file));
        // 循环工作表Sheet
        List<Map<String, String>> bookList = new LinkedList<Map<String, String>>();

        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    Map<String, String> bookInfo = new HashMap<String, String>();
                    XSSFCell bookId = hssfRow.getCell(0);
                    XSSFCell bookName = hssfRow.getCell(1);
                    bookInfo.put(getValue(bookId), getValue(bookName));
                    bookList.add(bookInfo);
                }
            }
        }
        return bookList;
    }

    /**
     * 处理value
     *
     * @param xssfCell
     * @return
     */
    public static String getValue(XSSFCell xssfCell) {
        if (xssfCell == null) {
            return "";
        }
        if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(decimalFormat.format(xssfCell.getNumericCellValue()));
        } else {
            // 返回字符串类型的值
            return String.valueOf(xssfCell.getStringCellValue());
        }
    }

    /**
     * MultipartFile 转换成File
     *
     * @param multfile 原文件类型
     * @return File
     * @throws IOException
     */
    /*public static File multipartToFile(MultipartFile multfile) throws IOException {
        CommonsMultipartFile cf = (CommonsMultipartFile) multfile;
        //这个myfile是MultipartFile的
        DiskFileItem fi = (DiskFileItem) cf.getFileItem();
        File file = fi.getStoreLocation();
        //手动创建临时文件
        if (file.length() < MIN_FILE_SIZE) {
            File tmpFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +
                    file.getName());
            multfile.transferTo(tmpFile);
            return tmpFile;
        }
        return file;
    }*/

}
