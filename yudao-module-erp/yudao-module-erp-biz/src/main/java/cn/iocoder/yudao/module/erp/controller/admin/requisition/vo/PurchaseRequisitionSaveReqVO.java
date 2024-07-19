package cn.iocoder.yudao.module.erp.controller.admin.requisition.vo;

import cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.order.ErpPurchaseOrderSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import javax.validation.constraints.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;

@Schema(description = "管理后台 - ERP 新增请购新增/修改 Request VO")
@Data
public class PurchaseRequisitionSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1157")
    private Long id;

    @Schema(description = "请购编号")
    private String requisitionCode;

    @Schema(description = "请购类型", example = "2")
    private String requisitionType;

    @Schema(description = "请购单时间")
    private LocalDateTime requisitionTime;

    @Schema(description = "预计交期")
    private LocalDateTime estimatedTime;

    @Schema(description = "关联项目")
    private String associationProject;

    @Schema(description = "附件")
    private String annex;

    @Schema(description = "审核类型", example = "2")
    private Integer status;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "请购清单列表")
    private List<PurchaseRequisitionSaveReqVO.Item> items;
    @Data
    public static class Item {

        @Schema(description = "请购项编号", example = "11756")
        private Long id;

        @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "3113")
        @NotNull(message = "产品编号不能为空")
        private Long productId;

        @Schema(description = "产品数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
        @NotNull(message = "产品数量不能为空")
        private BigDecimal count;

        @Schema(description = "备注", example = "随便")
        private String remark;

    }
}