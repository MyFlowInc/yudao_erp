package cn.iocoder.yudao.framework.excel.core.util;

import cn.iocoder.yudao.framework.excel.core.handler.SelectSheetWriteHandler;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.exception.ExcelGenerateException;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.handler.WriteHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 * Excel 工具类
 *
 * @author 芋道源码
 */
public class ExcelUtils {

//    /**
//     * 将列表以 Excel 响应给前端
//     *
//     * @param response  响应
//     * @param filename  文件名
//     * @param sheetName Excel sheet 名
//     * @param head      Excel head 头
//     * @param data      数据列表哦
//     * @param <T>       泛型，保证 head 和 data 类型的一致性
//     * @throws IOException 写入失败的情况
//     */
    public static <T> void write(HttpServletResponse response, String filename, String sheetName,
                                 Class<T> head, List<T> data) throws IOException {
        // 输出 Excel
        EasyExcel.write(response.getOutputStream(), head)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                // 基于 column 长度，自动适配。最大 255 宽度
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                // 基于固定 sheet 实现下拉框
                .registerWriteHandler(new SelectSheetWriteHandler(head))
                // 避免 Long 类型丢失精度
                .registerConverter(new LongStringConverter())
                .sheet(sheetName).doWrite(data);
        // 设置 header 和 contentType。写在最后的原因是，避免报错时，响应 contentType 已经被修改了
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
    }
//public static <T> void write(HttpServletResponse response, String filename, String sheetName,
//                             Class<T> head, List<T> data) throws IOException {
//    // Step 1: Generate Excel file with EasyExcel
//    ByteArrayOutputStream easyExcelData = new ByteArrayOutputStream();
//    EasyExcel.write(easyExcelData, head)
//            .autoCloseStream(false)
//            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
//            .registerWriteHandler(new SelectSheetWriteHandler(head))
//            .registerConverter(new LongStringConverter())
//            .sheet(sheetName)
//            .doWrite(data);
//
//    // Make sure data is correctly written to the output stream
//    easyExcelData.flush();
//    byte[] excelBytes = easyExcelData.toByteArray();
//    easyExcelData.close();
//
//    // Step 2: Add formulas using Apache POI
//    ByteArrayInputStream bais = new ByteArrayInputStream(excelBytes);
//    Workbook workbook = new XSSFWorkbook(bais);
//    Sheet sheet = workbook.getSheetAt(0);
//
//    // Add formulas to the sheet
//    int lastRowIndex = sheet.getLastRowNum();
//    Row formulaRow = sheet.createRow(lastRowIndex + 1);
//
//    // Example: Add a SUM formula in the first cell of the new row
//    Cell formulaCell = formulaRow.createCell(0);
//    formulaCell.setCellFormula("SUM(A2:A" + (lastRowIndex + 1) + ")");
//
//    // Write the updated workbook to ByteArrayOutputStream
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    workbook.write(baos);
//    workbook.close();
//    try (ServletOutputStream sos = response.getOutputStream()) {
//        baos.writeTo(sos);
//        sos.flush();
//    }
//    // Step 3: Send the Excel file as a response
//    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
//    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\""+ URLEncoder.encode(filename, StandardCharsets.UTF_8));
//
//
//}
    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        return EasyExcel.read(file.getInputStream(), head, null)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                .doReadAllSync();
    }

}
