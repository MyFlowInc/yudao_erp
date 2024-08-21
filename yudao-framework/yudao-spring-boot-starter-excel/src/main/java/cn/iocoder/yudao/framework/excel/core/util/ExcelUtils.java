package cn.iocoder.yudao.framework.excel.core.util;

import cn.iocoder.yudao.framework.excel.core.handler.SelectSheetWriteHandler;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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
        File tempFile = File.createTempFile("temp", ".xlsx");

        try (OutputStream tempFileOut = new FileOutputStream(tempFile)) {
            // 创建 ExcelWriter 对象
            ExcelWriter writer = EasyExcel.write(tempFileOut, dataClass)
                    .autoCloseStream(false)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();
            // 将每种类型的数据写入对应的 Sheet
            for (Map.Entry<Integer, List<T>> entry : dataMap.entrySet()) {
                Integer type = entry.getKey();
                List<T> data = entry.getValue();
                String sheetName = getTypeName(type);
                try {
                    // 创建 WriteSheet 实例并写入数据
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
                    writer.write(data, writeSheet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 完成写入后关闭 writer
            writer.finish();
            // 在这里开始使用 Apache POI 对临时文件进行后处理
            try (InputStream tempFileIn = new FileInputStream(tempFile);
                 XSSFWorkbook workbook = new XSSFWorkbook(tempFileIn)) {
                for (Map.Entry<Integer, List<T>> entry : dataMap.entrySet()) {
                    // 创建样式
                    CellStyle headerCellStyle = workbook.createCellStyle();

// 设置背景色为灰色
                    FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
                    // 使用浅灰色
                    short grayColor = IndexedColors.GREY_25_PERCENT.getIndex();
                    headerCellStyle.setFillPattern(fillPattern);
                    headerCellStyle.setFillForegroundColor(grayColor);

// 设置对齐方式（可选）
                    headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                    headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                        Sheet currentSheet = workbook.getSheetAt(i);
                        // 移除所有现有的合并区域
                        for (int j = currentSheet.getNumMergedRegions() - 1; j >= 0; j--) {
                            currentSheet.removeMergedRegion(j);
                        }
                        // 获取当前行数
                        int lastRowNum = currentSheet.getLastRowNum();

                        // 设置第11列的列名
                        Row headerRow = currentSheet.getRow(0);
                        if (headerRow == null) {
                            headerRow = currentSheet.createRow(0);
                        }

                        Cell headerCell = headerRow.getCell(7);
                        if (headerCell == null) {
                            headerCell = headerRow.createCell(7);
                        }
                        headerCell.setCellValue("金额合计");
                        // 应用样式到列名单元格
                        headerCell.setCellStyle(headerCellStyle);

                        if (lastRowNum > 1) {
                            // 多于一行数据时，设置合并区域和公式
                            currentSheet.addMergedRegion(new CellRangeAddress(1, lastRowNum, 7, 7));

                            Row firstRow = currentSheet.getRow(1);
                            if (firstRow == null) {
                                firstRow = currentSheet.createRow(1);
                            }

                            Cell mergedCell = firstRow.getCell(7);
                            if (mergedCell == null) {
                                mergedCell = firstRow.createCell(7);
                            }

                            mergedCell.setCellFormula("SUM(E2:E" + (lastRowNum + 1) + ")");
                            mergedCell.setCellStyle(createCellStyle(workbook));
                        } else if (lastRowNum == 1) {
                            // 仅一行数据时，直接设置 G2 的值到 K2
                            Row firstRow = currentSheet.getRow(1);
                            if (firstRow == null) {
                                firstRow = currentSheet.createRow(1);
                            }

                            Cell targetCell = firstRow.getCell(7);
                            if (targetCell == null) {
                                targetCell = firstRow.createCell(7);
                            }
                            Cell sourceCell = firstRow.getCell(4);
                            if (sourceCell != null && sourceCell.getCellType() == CellType.NUMERIC) {
                                targetCell.setCellValue(sourceCell.getNumericCellValue());
                            } else {
                                targetCell.setCellValue("");
                            }

                            targetCell.setCellStyle(createCellStyle(workbook));
                        }
                    }
                    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
                    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
                    try (OutputStream responseOut = response.getOutputStream()) {
                        workbook.write(responseOut);
                    }
                }
            } finally {
                tempFile.deleteOnExit();
            }
        }
    }
    public static String getTypeName(Integer type) {
        Map<Integer,String> map = new HashMap<Integer,String>();
        map.put(30,"其他支出");
        map.put(31,"其他收入");
        map.put(32,"领料");
        map.put(33,"还料");
        map.put(34,"ALL");
        return map.get(type);
    }
    // 方法：创建单元格样式
    private static CellStyle createCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("#,##0.000000"));
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }
    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        return EasyExcel.read(file.getInputStream(), head, null)
                // 不要自动关闭，交给 Servlet 自己处理
                .autoCloseStream(false)
                .doReadAllSync();
    }
}


