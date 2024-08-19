package cn.iocoder.yudao.framework.excel.core.util;

import cn.iocoder.yudao.framework.excel.core.handler.SelectSheetWriteHandler;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.exception.ExcelGenerateException;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.util.CellRangeAddress;
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

//    public static <T> void write(HttpServletResponse response, String filename, String sheetName,
//                                            Class<T> head, List<T> data) throws IOException {
//        // 先输出 Excel
//        File tempFile = File.createTempFile("temp", ".xlsx");
//        EasyExcel.write(tempFile, head)
//                .autoCloseStream(false)
//                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
//                .sheet(sheetName).doWrite(data);
//
//        // 然后使用 Apache POI 添加公式
//        try (FileInputStream fis = new FileInputStream(tempFile);
//             Workbook workbook = new XSSFWorkbook(fis)) {
//            // 获取第一个工作表
//            Sheet sheet = workbook.getSheetAt(0);
//            // 设置新列标题（假设是第4列）
//            Row headerRow = sheet.getRow(0);
//            if (headerRow == null) {
//                // 如果标题行不存在则创建
//                headerRow = sheet.createRow(0);
//            }
//            // 新列的列号
//            Cell headerCell = headerRow.createCell(15);
//            // 设置新列的名称
//            headerCell.setCellValue("计算结果");
//            int lastRowNum = sheet.getLastRowNum();
//            // 添加公式到新列 (假设公式在第4列，从第2行开始)
//            for (int i = 1; i <= lastRowNum; i++) {
//                Row row = sheet.getRow(i);
//                if (row == null) continue;
//                // 新列的列号
//                Cell cell = row.createCell(15);
//            }
//// 合并单元格，只在第一行添加值
//            if (lastRowNum > 1) {
//                // 合并范围：从第2行到最后一行，第16列
//                sheet.addMergedRegion(new CellRangeAddress(1, lastRowNum, 15, 15));
//                Row firstRow = sheet.getRow(1);
//                Cell mergedCell = null;
//                if (firstRow != null) {
//                    mergedCell = firstRow.getCell(15);
//                    // 在合并后的单元格中设置值
//                    mergedCell.setCellValue("计算结果");
//                }
//                // 在最后一行之后添加一个单元格来显示 A2 到 A 的总和
//                Row sumRow = sheet.createRow(lastRowNum + 1);
//                // 第16列
//                Cell sumCell = sumRow.createCell(15);
//                String sumFormula = "SUM(A2:A" + lastRowNum + ")";
//                sumCell.setCellFormula(sumFormula);
//
//                // 确保单元格中的公式被计算
//                CellStyle style = workbook.createCellStyle();
//                // 设置格式为数字
//                style.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
//                sumCell.setCellStyle(style);
//                // 计算公式的值并将其写入合并的单元格
//                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
//                evaluator.evaluateFormulaCell(sumCell);
//                double sumValue = sumCell.getNumericCellValue();
//
//                // 在合并的单元格中设置计算结果
//                mergedCell.setCellValue(sumValue);
//
//                // 设置响应头
//                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
//                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//                // 将修改后的文件写入响应输出流
//                try (OutputStream os = response.getOutputStream()) {
//                    workbook.write(os);
//                }
//            }
//            // 删除临时文件
//            tempFile.deleteOnExit();
//        }
//    }
    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        return EasyExcel.read(file.getInputStream(), head, null)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                .doReadAllSync();
    }

}
