package cn.iocoder.yudao.module.erp.controller.admin.requisition.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;

@Schema(description = "管理后台 - 新增请购新增/修改 Request VO")
@Data
public class PurchaseRequisitionSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1157")
    private Long id;

    @Schema(description = "请购编号")
    private String requisitionCode;

    @Schema(description = "请购类型", example = "2")
    private String requisitionType;

    @Schema(description = "请购单时间")
    private String requisitionTime;

    @Schema(description = "预计交期")
    private String estimatedTime;

    @Schema(description = "关联项目")
    private String associationProject;

    @Schema(description = "附件")
    private String annex;

    @Schema(description = "审核类型", example = "2")
    private String status;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}