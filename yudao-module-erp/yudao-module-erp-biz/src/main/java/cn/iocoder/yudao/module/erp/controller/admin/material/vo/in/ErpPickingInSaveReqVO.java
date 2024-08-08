package cn.iocoder.yudao.module.erp.controller.admin.material.vo.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpPickingInItemDO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author 15276
 */
@Schema(description = "管理后台 - ERP 领料出库单新增/修改 Request VO")
@Data
public class ErpPickingInSaveReqVO {

    @Schema(description = "领料单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21969")
    private Long id;

    @Schema(description = "领料号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "领料号不能为空")
    private String no;

    @Schema(description = "供应商编号", example = "7291")
    private Long supplierId;

    @Schema(description = "关联请购单", example = "16463")
    private Long associationRequisitionId;

    @Schema(description = "入库时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "入库时间不能为空")
    private LocalDateTime inTime;

    @Schema(description = "合计数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "6094")
    @NotNull(message = "合计数量不能为空")
    private BigDecimal totalCount;

    @Schema(description = "合计金额，单位：元", requiredMode = Schema.RequiredMode.REQUIRED, example = "11332")
    @NotNull(message = "合计金额，单位：元不能为空")
    private BigDecimal totalPrice;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "你猜")
    private String remark;

    @Schema(description = "附件 URL", example = "https://www.iocoder.cn")
    private String fileUrl;

//    @Schema(description = "ERP 领料出库单项列表")
//    private List<ErpPickingInItemDO> pickingInItems;

    @Schema(description = "ERP 领料出库单项列表")
    private List<ErpPickingInSaveReqVO.Item> items;

    @Data
    public static class Item {
        @Schema(description = "id")
        private Long id;

        @NotNull(message = "领料单id不可为空")
        private Long inId;

        @Schema(description = "仓库id")
        @NotNull(message = "仓库id不可为空")
        private Long warehouseId;
        /**
         * 关联批次id
         */
        @Schema(description = "批次ID")
        @NotNull(message = "批次ID不能为空")
        private Long associatedBatchId;
        /**
         * 关联请购项
         */
        @Schema(description = "请购项ID")
        private Long associationRequisitionProductId;
        /**
         * 产品编号
         */
        @Schema(description = "产品编号")
        @NotNull(message = "产品编号不能为空")
        private Long productId;
        /**
         * 产品单位编号
         */
        @Schema(description = "产品单位编号")
        private Long productUnitId;
        /**
         * 产品单价
         */
        @Schema(description = "产品单价")
        private BigDecimal productPrice;
        /**
         * 产品数量
         */
        @Schema(description = "产品数量")
        private BigDecimal count;
        /**
         * 合计金额，单位：元
         */
        @Schema(description = "合计金额")
        private BigDecimal totalPrice;
        /**
         * 备注
         */
        @Schema(description = "备注")
        private String remark;
    }
}