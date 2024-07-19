package cn.iocoder.yudao.module.erp.controller.admin.requisition.vo;

import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 新增请购 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PurchaseRequisitionRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1157")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "请购编号")
    @ExcelProperty("请购编号")
    private String requisitionCode;

    @Schema(description = "请购类型", example = "2")
    @ExcelProperty("请购类型")
    private String requisitionType;

    @Schema(description = "请购单时间")
    @ExcelProperty("请购单时间")
    private String requisitionTime;

    @Schema(description = "预计交期")
    @ExcelProperty("预计交期")
    private String estimatedTime;

    @Schema(description = "关联项目")
    @ExcelProperty("关联项目")
    private String associationProject;

    @Schema(description = "附件")
    @ExcelProperty("附件")
    private String annex;

    @Schema(description = "审核类型", example = "2")
    @ExcelProperty("审核类型")
    private String status;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子项产品")
    List<RequisitionProductDO> children;
}