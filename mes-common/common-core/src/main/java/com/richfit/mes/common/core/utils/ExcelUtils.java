package com.richfit.mes.common.core.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

/**
 * Excel工具类
 *
 * @author Joe
 */
public class ExcelUtils {

    /**
     * Excel2003最大行数
     */
    private static final int MAX_ROW_2003 = 65535;

    /**
     * 2007版最大行数
     */
    private static final int MAX_ROW_2007 = 1048575;

    /**
     * 从Excel中导入内容到List集合
     *
     * @param file       待处理的Excel文件
     * @param type       对应转换的bean类名
     * @param fieldNames 需要转换为bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param fileName   文件名
     * @return 集合
     * @throws IOException IO异常
     */
    public static <T> List<T> importExcel(File file, Class<T> type,
                                          String[] fieldNames, String fileName) throws IOException {
        return importExcel(file, type, fieldNames, 0, 0, 0, fileName);
    }

    /**
     * 从Excel中导入内容到List集合
     *
     * @param file        待处理的Excel文件
     * @param type        对应转换的bean类名
     * @param fieldNames  需要转换为bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param beginRow    需要导入的开始行，小于等于-1都表示从Excel第一行开始导入
     * @param beginColumn 需要导入的开始列，小于等于-1表示Excel第一列开始
     * @param sheetIndex  工作表下标
     * @param fileName    文件名
     * @return 集合
     * @throws IOException IO异常
     */
    public static <T> List<T> importExcel(File file, Class<T> type, String[] fieldNames,
                                          int beginRow, int beginColumn, int sheetIndex,
                                          String fileName) throws IOException {
        return importExcel(file, type, fieldNames, beginRow, -1, beginColumn, sheetIndex, fileName);
    }

    /**
     * 从Excel中导入内容到List集合
     *
     * @param file        待处理的Excel文件
     * @param type        对应转换的bean类名
     * @param fieldNames  需要转换为bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param beginRow    需要导入的开始行，小于等于-1都表示从Excel第一行开始导入
     * @param endRow      需要导入的结束行，小于等于-1都表示Excel最后一行结束导入，如果超过Excel最后一行，则以Excel的最后一行为结束
     * @param beginColumn 需要导入的开始列，小于等于-1表示Excel第一列开始
     * @param sheetIndex  工作表下标
     * @return 集合
     * @throws IOException IO异常
     */
    private static <T> List<T> importExcel(File file, Class<T> type, String[] fieldNames,
                                           int beginRow, int endRow, int beginColumn,
                                           int sheetIndex, String fileName) throws IOException {
        // 创建文件输入流
        FileInputStream in = new FileInputStream(file);
        // 创建Excel工作簿（包括2003和2007版）
        Workbook workbook = createWorkbook(FileUtils.getFilenameExtension(fileName), in);
        // 根据下标获取Excel工作表
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        return importExcel(type, fieldNames, beginRow, endRow, beginColumn, -1, sheet);
    }

    /**
     * 从Excel中导入内容到List集合
     *
     * @param inputStream 待处理的Excel文件
     * @param type        对应转换的bean类名
     * @param fieldNames  需要转换为bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param fileName    文件名
     * @return 集合
     * @throws IOException IO异常
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> type,
                                          String[] fieldNames, String fileName) throws IOException {
        return importExcel(inputStream, type, fieldNames, 0, 0, 0, fileName);
    }

    /**
     * 从Excel中导入内容到List集合
     *
     * @param inputStream 待处理的Excel文件
     * @param type        对应转换的bean类名
     * @param fieldNames  需要转换为bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param beginRow    需要导入的开始行，小于等于-1都表示从Excel第一行开始导入
     * @param beginColumn 需要导入的开始列，小于等于-1表示Excel第一列开始
     * @param sheetIndex  工作表下标
     * @param fileName    文件名
     * @return 集合
     * @throws IOException IO异常
     */
    public static <T> List<T> importExcel(InputStream inputStream, Class<T> type, String[] fieldNames,
                                          int beginRow, int beginColumn, int sheetIndex,
                                          String fileName) throws IOException {
        return importExcel(inputStream, type, fieldNames, beginRow, -1, beginColumn, sheetIndex, fileName);
    }

    /**
     * 从Excel中导入内容到List集合
     *
     * @param inputStream 待处理的Excel文件
     * @param type        对应转换的bean类名
     * @param fieldNames  需要转换为bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param beginRow    需要导入的开始行，小于等于-1都表示从Excel第一行开始导入
     * @param endRow      需要导入的结束行，小于等于-1都表示Excel最后一行结束导入，如果超过Excel最后一行，则以Excel的最后一行为结束
     * @param beginColumn 需要导入的开始列，小于等于-1表示Excel第一列开始
     * @param sheetIndex  工作表下标
     * @return 集合
     * @throws IOException IO异常
     */
    private static <T> List<T> importExcel(InputStream inputStream, Class<T> type,
                                           String[] fieldNames, int beginRow, int endRow, int beginColumn,
                                           int sheetIndex, String fileName) throws IOException {
        // 创建Excel工作簿（包括2003和2007版）
        Workbook workbook = createWorkbook(FileUtils.getFilenameExtension(fileName), inputStream);
        // 根据下标获取Excel工作表
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        return importExcel(type, fieldNames, beginRow, endRow, beginColumn, -1, sheet);
    }

    /**
     * 创建Excel工作簿
     *
     * @param fileExtension Excel文件名
     * @param in            文件输入流
     * @return 工作簿
     * @throws IOException IO异常
     */
    private static Workbook createWorkbook(String fileExtension, InputStream in) throws IOException {
        Workbook workbook;
        String str = "xlsx";
        if (str.equals(fileExtension)) {
            // 2007版excel
            workbook = (null != in ? new XSSFWorkbook(in) : new XSSFWorkbook());
        } else {
            // 2003版excel
            workbook = (null != in ? new HSSFWorkbook(in) : new HSSFWorkbook());
        }
        return workbook;
    }

    /**
     * 从Excel中导入内容到List集合
     *
     * @param type        对应转换的bean类名
     * @param fieldNames  需要转换为bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param beginRow    需要导入的开始行，小于等于-1都表示从Excel第一行开始导入
     * @param endRow      需要导入的结束行，小于等于-1都表示Excel最后一行结束导入，如果超过Excel最后一行，则以Excel的最后一行为结束
     * @param beginColumn 需要导入的开始列，小于等于-1表示Excel第一列开始
     * @param endColumn   需要导入的结束列，小于等于-1表示Excel最后一列结束，如果超过Excel最后一列，则以Excel的最后一列为结束
     * @param sheet       工作表
     * @return 集合
     */
    private static <T> List<T> importExcel(Class<T> type, String[] fieldNames, int beginRow,
                                           int endRow, int beginColumn, int endColumn, Sheet sheet) {
        // 验证起始行是否符合标准
        if (0 > beginRow) {
            beginRow = sheet.getFirstRowNum();
        }
        if (0 > endRow) {
            endRow = sheet.getLastRowNum();
        }
        List<T> list = new ArrayList<>();
        Row row;
        T t;
        int i = beginRow;
        while (i <= endRow) {
            // 获取工作表中的某一行
            row = sheet.getRow(i);
            if (null == row) {
                i++;
                continue;
            }
            // 验证起始列是否符合标准
            if (0 > beginColumn) {
                beginColumn = row.getFirstCellNum();
            } else if (beginColumn > row.getLastCellNum()) {
                throw new IllegalArgumentException("Start column is more than last column!");
            }
            if (0 > endColumn) {
                endColumn = row.getLastCellNum();
            } else if (endColumn > row.getLastCellNum()) {
                endColumn = row.getLastCellNum();
            }
            if (0 > endColumn - beginColumn) {
                throw new IllegalArgumentException("Start column is more than end column!");
            }
            // 通过反射创建class的实例
            try {
                t = type.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("The bean class object cannot be instantiated!", e);
            }
            // 解析excel行列数据对应到创建的Bean中
            int j = beginColumn, index = 0;
            while (j < endColumn && index < fieldNames.length) {
                Cell cell = row.getCell(j);
                if (cell == null) {
//                    i++;
                    j++;
                    index++;
                    continue;
                }
                String propertyName = fieldNames[index];
                try {
                    Object value = getCellValue(cell, getPropertyType(t, propertyName));
                    if(value != null){
                        setPropertyValue(t, propertyName, value);
                    }
                } catch (Exception e) {
                    throw new IllegalArgumentException(type.getName() + "." +
                            propertyName + " cannot be accessed!", e);
                }
                j++;
                index++;
            }
            list.add(t);
            i++;
        }
        return list;
    }

    /**
     * 获取excel单元格中的值
     *
     * @param cell excel单元格
     * @param type 属性的返回类型Class
     * @return obj
     */
    private static Object getCellValue(Cell cell, Class<?> type) {
        Object value;
        try {
            if (type == Date.class) {
                value = cell.getDateCellValue();
            } else {
                value = getCellValue(cell, cell.getCellTypeEnum());

                if (null == value) {
                    return null;
                }
                if (int.class == type || Integer.class == type) {
                    if (value instanceof String) {
                        value = !StringUtils.isEmpty(value) ?
                                Integer.valueOf((String)value) : Integer.valueOf(0);
                    } else if (value instanceof Double) {
                        value = ((Double)value).intValue();
                    }
                } else if (double.class == type || Double.class == type) {
                    if (value instanceof String) {
                        value = !StringUtils.isEmpty(value) ?
                                Double.parseDouble((String)value) : 0;
                    } else if (value instanceof Integer) {
                        value = Double.valueOf((Integer)value);
                    }
                } else if (float.class == type || Float.class == type) {
                    if (value instanceof String) {
                        value = !StringUtils.isEmpty(value) ?
                                Float.parseFloat((String)value) : 0;
                    } else if (value instanceof Integer) {
                        value = Float.valueOf((Integer)value);
                    }
                }else if (boolean.class == type) {
                    if (double.class == value.getClass() || int.class == value.getClass()) {
                        value = 1 == (int)value;
                    } else if (value instanceof String) {
                        value = ((String)value).trim();
                        value = "1".equals(value)
                                || "y".equalsIgnoreCase((String)value)
                                || "yes".equalsIgnoreCase((String)value)
                                || "true".equalsIgnoreCase((String)value);
                    }
                }
            }
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取excel单元格中的值
     *
     * @param cell     excel单元格
     * @param cellType excel单元格类型
     * @return obj
     */
    private static Object getCellValue(Cell cell, CellType cellType) {
        Object value = null;
        switch (cellType) {
            case BLANK:
                return null;
            case FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            case NUMERIC:
                cell.setCellType(CellType.STRING);
                String temp = cell.getStringCellValue();
                // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                value = temp.contains(".") ? String.valueOf(new Double(temp)).trim() : temp.trim();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            default:
        }
        return value;
    }

    /**
     * 导出内容到Excel文件
     *
     * @param fileName      可包含文件路径的Excel文件名称
     * @param columnHeaders Excel文件列头
     * @param fieldNames    Bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param dataMap       导出到Excel的多个sheet集合的Map
     * @return 文件
     * @throws IOException IO异常
     */
    public static File exportExcel(String fileName, String[] columnHeaders,
                                   String[] fieldNames, Map<String, Collection<?>> dataMap) throws IOException {
        if (null != dataMap) {
            Workbook workbook = null;
            String fileExtension = FileUtils.getFilenameExtension(fileName);
            for (Map.Entry<String, Collection<?>> entry : dataMap.entrySet()) {
                String k = entry.getKey();
                workbook = createWorkbook(workbook, fileExtension,
                        dataMap.get(k), columnHeaders, fieldNames, k);
            }
            if (null != workbook) {
                return writeFile(fileName, workbook);
            }
        }
        return null;
    }

    /**
     * 导出内容到Excel文件
     *
     * @param fileName      可包含文件路径的Excel文件名称
     * @param collection    导出到Excel的内容集合
     * @param columnHeaders Excel文件列头
     * @param fieldNames    Bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @return 文件
     * @throws IOException IO异常
     */
    public static File exportExcel(String fileName, Collection<?> collection,
                                   String[] columnHeaders, String[] fieldNames) throws IOException {
        File file;
        String fileExtension = FileUtils.getFilenameExtension(fileName);
        int maxRow = MAX_ROW_2003;
        String str = "xlsx";
        if (str.equals(fileExtension)) {
            maxRow = MAX_ROW_2007;
        }
        file = maxRow < collection.size() ?
                writeFile(fileName, loopWorkbook(null, fileExtension, new ArrayList<>(collection),
                        columnHeaders, fieldNames, 0, maxRow, maxRow, 1)) :
                writeFile(fileName, createWorkbook(null, fileExtension, collection,
                        columnHeaders, fieldNames, "sheet1"));
        return file;
    }

    /**
     * 导出Excel（浏览器）
     *
     * @param fileName      可包含文件路径的Excel文件名称
     * @param collection    导出到Excel的内容集合
     * @param columnHeaders Excel文件列头
     * @param fieldNames    Bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @throws IOException IO异常
     */
    public static void exportExcel(String fileName, Collection<?> collection, String[] columnHeaders,
                                   String[] fieldNames, HttpServletResponse response) throws IOException {
        String fileExtension = FileUtils.getFilenameExtension(fileName);
        int maxRow = MAX_ROW_2003;
        String str = "xlsx";
        if (str.equals(fileExtension)) {
            maxRow = MAX_ROW_2007;
        }
        write(response, collection.size() > maxRow ?
                loopWorkbook(null, fileExtension, new ArrayList<>(collection),
                        columnHeaders, fieldNames, 0, maxRow, maxRow, 1) :
                createWorkbook(null, fileExtension, collection, columnHeaders,
                        fieldNames, "sheet1"), fileName);
    }

    /**
     * 循环的创建Excel工作簿
     * (当数据集合大于excel工作表的最大行数时，创建多个工作表填充数据,sheet1、sheet2、sheet3...)
     *
     * @param workbook      excel工作簿
     * @param fileExtension 文件扩展名
     * @param columnHeaders Excel文件列头
     * @param fieldNames    Bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param list          导出到Excel的内容集合
     * @param fromIndex     list集合开始下标（包含本身）
     * @param toIndex       list集合结束下标（不包含本身）
     * @param maxRow        excel可支持的最大行数
     * @param sheetIndex    工作表下标
     * @return 工作簿
     * @throws IOException IO异常
     */
    private static Workbook loopWorkbook(Workbook workbook, String fileExtension, List<Object> list,
                                         String[] columnHeaders, String[] fieldNames, int fromIndex,
                                         int toIndex, int maxRow, int sheetIndex) throws IOException {
        if (toIndex == list.size()) {
            return createWorkbook(workbook, fileExtension, list.subList(fromIndex, toIndex),
                    columnHeaders, fieldNames, "sheet" + sheetIndex);
        } else {
            workbook = createWorkbook(workbook, fileExtension, list.subList(fromIndex, toIndex),
                    columnHeaders, fieldNames, "sheet" + sheetIndex);
            return loopWorkbook(workbook, fileExtension, list, columnHeaders, fieldNames, toIndex,
                    Math.min((toIndex + maxRow), list.size()), maxRow, ++sheetIndex);
        }
    }

    /**
     * 导出内容到Excel文件
     *
     * @param fileExtension 可包含文件路径的Excel文件名称
     * @param collection    导出到Excel的内容集合
     * @param columnHeaders Excel文件列头
     * @param fieldNames    Bean的属性名称集合，属性的顺序必须和列的顺序保持一致
     * @param sheetName     工作表名称
     * @return 工作簿
     * @throws IOException IO异常
     */
    private static Workbook createWorkbook(Workbook workbook, String fileExtension,
                                           Collection<?> collection, String[] columnHeaders,
                                           String[] fieldNames, String sheetName) throws IOException {
        if (null == workbook) {
            workbook = createWorkbook(fileExtension, null);
        }
        Sheet sheet = workbook.createSheet(sheetName);
        // 产生表格标题行
        int rowIndex = sheet.getLastRowNum();
        if (null != columnHeaders && 0 < columnHeaders.length) {
            Row row = sheet.createRow(rowIndex);
            IntStream.range(0, columnHeaders.length).forEach(i -> {
                Cell cell = row.createCell(i);
                cell.setCellValue(columnHeaders[i]);
            });
            rowIndex++;
        }
        // 产生Excel表数据
        Iterator<?> iter = collection.iterator();
        // 属性类型
        Class<?> propertyType;
        // 属性值
        Object value;
        // 日期格式
        CellStyle dateCellStyle = null;
        while (iter.hasNext()) {
            Row row = sheet.createRow(rowIndex++);
            Object t = iter.next();
            if (null == fieldNames) {
                fieldNames = getFieldNames(t);
            }
            int columnIndex = 0;
            while (columnIndex < fieldNames.length) {
                try {
                    propertyType = getPropertyType(t, fieldNames[columnIndex]);
                    value = getPropertyValue(t, fieldNames[columnIndex]);
                } catch (Exception e) {
                    throw new IllegalArgumentException(t.getClass().getName()
                            + "." + fieldNames[columnIndex]
                            + " cannot be accessed!", e);
                }
                if (null == value) {
                    columnIndex++;
                    continue;
                }
                Cell cell = row.createCell(columnIndex);
                if (Boolean.class == propertyType) {
                    cell.setCellValue((Boolean)value ? "true" : "false");
                } else if (Date.class == propertyType) {
                    if (null == dateCellStyle) {
                        dateCellStyle = createCellStyle(workbook);
                    }
                    cell.setCellValue((Date)value);
                    cell.setCellStyle(dateCellStyle);
                } else if (double.class == propertyType || Double.class == propertyType) {
                    cell.setCellValue(new DecimalFormat("0.00#").format(value));
                } else if (int.class == propertyType || Integer.class == propertyType) {
                    cell.setCellValue((Integer)value);
                } else {
                    cell.setCellValue(value.toString());
                }
                columnIndex++;
            }
        }
        return workbook;
    }

    /**
     * 把创建的excel工作簿写入到物理路径
     *
     * @param fileName 包含路径的文件名称
     * @param workbook excel工作簿
     * @return 文件
     * @throws IOException IO异常
     */
    private static File writeFile(String fileName, Workbook workbook) throws IOException {
        File file = FileUtils.createFile(fileName);
        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();
        return file;
    }

    /**
     * 把创建的excel用流返回
     *
     * @param response 包含路径的文件名称
     * @param workbook excel工作簿
     * @throws IOException IO异常
     */
    private static void write(HttpServletResponse response, Workbook workbook, String fileName)
            throws IOException {
        response.setContentType("application/octet-stream");
        //默认Excel名称
        response.setHeader("Content-disposition",
                String.format("attachment;filename=%s", URLEncoder.encode(fileName, "UTF-8")));
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }

    /**
     * 为日期类型创建工作表中单元格样式
     *
     * @param workbook 工作簿
     * @return 样式
     */
    private static CellStyle createCellStyle(Workbook workbook) {
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-MM-dd"));
        return dateCellStyle;
    }

    /**
     * 通过反射处理，设置某个对象中的某个属性值
     *
     * @param obj          指定对象
     * @param propertyName 对象属性名称
     * @param value        对象属性值
     */
    private static void setPropertyValue(Object obj, String propertyName,
                                         Object value) throws IllegalArgumentException {
        if (null == obj) {
            return;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                if (property.getName().equals(propertyName)) {
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 通过反射处理，获取某个对象中的某个属性值
     *
     * @param obj          指定对象
     * @param propertyName 对象属性名称
     * @return obj
     */
    private static Object getPropertyValue(Object obj, String propertyName) throws IllegalArgumentException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            @SuppressWarnings("rawtypes")
            Map m = (Map)obj;
            return m.get(propertyName);
        } else {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor property : propertyDescriptors) {
                    if (property.getName().equals(propertyName)) {
                        Method getter = property.getReadMethod();
                        return getter.invoke(obj);
                    }
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return null;
    }

    /**
     * 通过反射处理，获取某个对象中的某个属性的返回类型Class
     *
     * @param obj          指定对象
     * @param propertyName 对象属性名称
     * @return Class
     */
    private static Class<?> getPropertyType(Object obj, String propertyName) throws IllegalArgumentException {
        if (null == obj) {
            return null;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                if (property.getName().equals(propertyName)) {
                    return property.getPropertyType();
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return null;
    }

    /**
     * 通过反射处理，获取某个对象中的所有属性名称，并添加到字符串数组返回
     *
     * @param obj 指定对象
     * @return String数组
     */
    private static String[] getFieldNames(Object obj) throws IllegalArgumentException {
        if (null == obj) {
            return null;
        }
        String[] fieldNames;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            fieldNames = new String[propertyDescriptors.length - 1];
            int i = 0;
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!"class".equals(key)) {
                    fieldNames[i++] = key;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return fieldNames;
    }

}
