package me.lv.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.formula.functions.T;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lzw
 * @date 2018/4/17
 */
public class ExcelUtil {

    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    private static final String POINT = ".";
    private static Pattern PATTERN = Pattern.compile("^(\\-|\\+)?\\d+(\\.\\d+)?$");
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
        logger.debug(">>>>>> import excel start ...");
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
        logger.debug(">>>>>> import excel end ...");
    }

    /**
     * 新的EXCEL输出工具类
     *
     * @param columnNameStr
     *            指定输出的字段名,逗号分开
     * @param columnFieldStr
     *            指定输出的字段,逗号分开
     * @param dataList
     * @throws IOException
     */
    public static <T> void exportExcel(String columnNameStr, String columnFieldStr, List<T> dataList) throws Exception {
        logger.debug(">>>>>> export excel start ...");
        String[] columnNames = URLDecoder.decode(columnNameStr, "UTF-8").split(",");
        String[] columnFields = columnFieldStr.split(",");
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet();
        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth(15);
        // 生成一个样式
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 10);
        titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        titleStyle.setFont(titleFont);

        HSSFCellStyle fieldStyle = workbook.createCellStyle();
        fieldStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        fieldStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        fieldStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        fieldStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        fieldStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
        fieldStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        HSSFFont fieldFont = workbook.createFont();
        fieldFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        fieldFont.setFontName("宋体");
        fieldFont.setFontHeightInPoints((short) 10);
        fieldStyle.setFont(fieldFont);

        HSSFRow row = sheet.createRow(0);
        for (int i = 0; i < columnNames.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(titleStyle);
            HSSFRichTextString text = new HSSFRichTextString(columnNames[i]);
            cell.setCellValue(text);
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = dataList.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = it.next();
            for (int i = 0; i < columnFields.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellStyle(fieldStyle);
                String fieldName = columnFields[i];
                Object value = getValue(t, fieldName);
                String textValue = getString(workbook, sheet, row, index, i, value);
                // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                if (textValue != null) {
                    Matcher matcher = PATTERN.matcher(textValue);
                    if (matcher.matches()) {
                        // 是数字当作double处理
                        cell.setCellValue(Double.parseDouble(textValue));
                    } else {
                        HSSFRichTextString richString = new HSSFRichTextString(textValue);
                        cell.setCellValue(richString);
                    }
                }
            }
        }
        FileOutputStream fout = new FileOutputStream("D:/test.xls");
        workbook.write(fout);
        logger.info(">>>>>> 导出成功！");
        fout.close();
        logger.debug(">>>>>> export excel end ...");
    }

    /**
     * 判断值的类型后进行强制类型转换
     * @param workbook
     * @param sheet
     * @param row
     * @param index
     * @param i
     * @param value
     * @return
     */
    private static String getString(HSSFWorkbook workbook, HSSFSheet sheet, HSSFRow row, int index, int i, Object value) {
        String textValue = null;
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        if (value instanceof String) {
            textValue = value.toString();
        } else if (value instanceof Integer) {
            textValue = value.toString();
        } else if (value instanceof BigInteger) {
            textValue = value.toString();
        } else if (value instanceof Float) {
            textValue = value.toString();
        } else if (value instanceof Double) {
            textValue = value.toString();
        } else if (value instanceof Long) {
            textValue = value.toString();
        } else if (value instanceof BigDecimal) {
            textValue = value.toString();
        } else if (value instanceof Boolean) {
            boolean bValue = (Boolean) value;
            textValue = "是";
            if (!bValue) {
                textValue = "否";
            }
        } else if (value instanceof Date) {
            Date date = (Date) value;
            textValue = simpleDateFormat.format(date);
        } else if (value instanceof byte[]) {
            // 有图片时，设置行高为60px;
            row.setHeightInPoints(60);
            // 设置图片所在列宽度为80px,注意这里单位的一个换算
            sheet.setColumnWidth(i, (short) (35.7 * 80));
            byte[] bsValue = (byte[]) value;
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
                    1023, 255, (short) 6, index, (short) 6, index);
            anchor.setAnchorType(2);
            patriarch.createPicture(anchor, workbook.addPicture(
                    bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
        } else {
            // 其它数据类型都当作字符串简单处理
            if (value != null) {
                textValue = value.toString();
            }
        }
        return textValue;
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
     * 递归调用，获取多层包装类中的值
     * @param obj
     * @param colField
     * @return
     * @throws Exception
     */
    private static Object getValue(Object obj, String colField) throws Exception{
        Object value;
        if (colField.indexOf(POINT) > 0) {
            String fieldName = colField.substring(0, colField.indexOf(POINT));
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldObj = field.get(obj);
            String nextField = colField.substring(colField.indexOf(POINT) + 1);
            value = getValue(fieldObj, nextField);
        } else {
            Field field = obj.getClass().getDeclaredField(colField);
            field.setAccessible(true);
            value = field.get(obj);
        }
        return value;
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
