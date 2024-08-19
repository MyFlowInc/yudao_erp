package cn.iocoder.yudao.framework.excel.core.util;

import cn.iocoder.yudao.framework.excel.core.handler.SelectSheetWriteHandler;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.io.IOException;
import java.util.Map;

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

    public static <T> void writeCostItemMap(HttpServletResponse response, String filename,
                                            Map<Integer, List<T>> dataMap, Class<T> dataClass) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("temp", ".xlsx");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // 遍历每个类型的列表，将数据写入不同的 Sheet
            for (Map.Entry<Integer, List<T>> entry : dataMap.entrySet()) {
                Integer type = entry.getKey();
                List<T> data = entry.getValue();

                // 创建 sheet
                Sheet sheet = workbook.createSheet("Type_" + type);

                // 使用 EasyExcel 写入数据到 sheet
                EasyExcel.write(tempFile, dataClass)
                        .autoCloseStream(false)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .sheet("Type_" + type)
                        .doWrite(data);

                // 添加公式到新列 (假设公式在第16列，从第2行开始)
                int lastRowNum = sheet.getLastRowNum();
                for (int i = 1; i <= lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    // 创建第16列
                    Cell cell = row.createCell(15);
                    cell.setCellFormula("SUM(A2:A" + (lastRowNum + 1) + ")");
                }
                // 合并单元格
                if (lastRowNum > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(1, lastRowNum, 15, 15));
                    Row firstRow = sheet.getRow(1);
                    Cell mergedCell = firstRow.getCell(15);

                    if (mergedCell == null) {
                        mergedCell = firstRow.createCell(15);
                    }
                    // 在合并后的单元格中设置公式, 公式范围调整为 A2:A[lastRowNum + 1]
                    String sumFormula = "SUM(A2:A" + (lastRowNum + 1) + ")";
                    mergedCell.setCellFormula(sumFormula);

                    // 格式化单元格，以显示合适的小数位数
                    CellStyle cellStyle = workbook.createCellStyle();
                    DataFormat format = workbook.createDataFormat();
                    // 设置显示六位小数
                    cellStyle.setDataFormat(format.getFormat("#,##0.000000"));
                    // 设置水平居中
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                    // 设置垂直居中
                    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    mergedCell.setCellStyle(cellStyle);
                }
            }

            // 设置响应头
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");

            // 将修改后的文件写入响应输出流
            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
            }
        } finally {
            // 删除临时文件
            tempFile.deleteOnExit();
        }
    }
    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        return EasyExcel.read(file.getInputStream(), head, null)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                .doReadAllSync();
    }

}
