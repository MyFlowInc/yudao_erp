package cn.iocoder.yudao.module.erp.controller.admin.productbatch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - ERP产品批次信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpProductBatchRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8702")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "批次名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("批次名称")
    private String name;

    @Schema(description = "批次类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("批次类型")
    private String type;

    @Schema(description = "关联产品", requiredMode = Schema.RequiredMode.REQUIRED, example = "7724")
    @ExcelProperty("关联产品")
    private String associationProductId;
    @Schema(description = "产品单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "4601")
    @ExcelProperty("产品单价")
    private BigDecimal unitPrice;
    /**
     * 批次入库数量
     */
    @Schema(description = "批次入库数量")
    @ExcelProperty("批次入库数量")
    private Integer inventoryQuantity;

    @Schema(description = "批次编号")
    @ExcelProperty("批次编号")
    private String code;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "关联产品名称", example = "哈哈")
    private String productName;
}