package cn.iocoder.yudao.module.erp.controller.admin.productbatch.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - ERP产品批次信息新增/修改 Request VO")
@Data
public class ErpProductBatchSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "8702")
    private Long id;

    @Schema(description = "批次名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "批次名称不能为空")
    private String name;

    @Schema(description = "批次类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "批次类型不能为空")
    private String type;

    @Schema(description = "关联产品", requiredMode = Schema.RequiredMode.REQUIRED, example = "7724")
    @NotEmpty(message = "关联产品不能为空")
    private String associationProductId;
    @Schema(description = "关联批次", requiredMode = Schema.RequiredMode.REQUIRED, example = "7724")
    @NotEmpty(message = "关联批次不可为空")
    private String associatedBatchId;

    @Schema(description = "产品单价", requiredMode = Schema.RequiredMode.REQUIRED, example = "4601")
    @NotEmpty(message = "产品单价不能为空")
    private BigDecimal unitPrice;

    @Schema(description = "批次编号")
    private String code;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

}