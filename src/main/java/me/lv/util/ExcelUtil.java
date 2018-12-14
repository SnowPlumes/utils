package me.lv.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *
 * @author lzw
 * @date 2018/4/17
 */
public class ExcelUtil {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    private static Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 默认单元格内容为数字时格式
     */
    private static DecimalFormat decimalFormat = new DecimalFormat("0");
    /**
     * 默认单元格格式化日期字符串
     */
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

    public static void importExcel(File file){
        logger.info(">>>>>> importExcel start ...");
        if(checkExcelVaild(file)) {
            return;
        }
        //装载流
        Workbook workbook = null;
        try {
            workbook = getWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
            Sheet sheet = workbook.getSheetAt(numSheet);
            if (sheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        logger.info(">>>>>> 第{}行 第{}列 数据 : {}", rowNum + 1, cellNum + 1, getValue(cell));
                    }
                }
            }
        }
        logger.info(">>>>>> importExcel end ...");
    }

    public static void exportExcel() {
        // TODO
    }

    /**
     * 判断Excel的版本,获取Workbook
     * @param file
     * @return
     * @throws IOException
     */
    private static Workbook getWorkbook(File file) throws IOException{
        Workbook workbook = null;
        if(file.getName().endsWith(EXCEL_XLS)){
            workbook = new HSSFWorkbook(new FileInputStream(file));
        }else if(file.getName().endsWith(EXCEL_XLSX)){
            workbook = new XSSFWorkbook(new FileInputStream(file));
        }
        return workbook;
    }


    /**
     * 判断文件是否是excel
     * @throws Exception
     */
    private static boolean checkExcelVaild(File file) {
        if(!file.exists()){
            logger.error(">>>>>> 文件不存在");
            return true;
        }
        boolean isExcel = !(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)));
        if(isExcel){
            logger.error(">>>>>> 文件不是Excel");
            return true;
        }
        return false;
    }

    /**
     * 处理value
     *
     * @param cell
     * @return
     */
    private static String getValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(decimalFormat.format(cell.getNumericCellValue()));
        } else {
            // 返回字符串类型的值
            return String.valueOf(cell.getStringCellValue());
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
