package cn.iocoder.yudao.module.erp.controller.admin.material.vo.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - ERP 领料出库单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpPickingInRespVO {

    @Schema(description = "领料单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "21969")
    @ExcelProperty("领料单编号")
    private Long id;

    @Schema(description = "领料号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("领料号")
    private String no;

    @Schema(description = "产品名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品名")
    private String productNames;

    @Schema(description = "供应商编号", example = "7291")
    @ExcelProperty("供应商编号")
    private Long supplierId;

    @Schema(description = "关联请购单", example = "16463")
    @ExcelProperty("关联请购单")
    private Long associationRequisitionId;
    @Schema(description = "关联请购单NO", example = "16463")
    @ExcelProperty("关联请购单NO")
    private String associationRequisitionNo;

    @Schema(description = "关联请购单编号", example = "16463")
    @ExcelProperty("关联请购单编号")
    private String requisitionCode;

    @Schema(description = "关联项目", example = "16463")
    @ExcelProperty("关联项目")
    private Long associationProjectId;

    @Schema(description = "关联项目名称", example = "16463")
    @ExcelProperty("关联项目名称")
    private String associationProjectName;

    @Schema(description = "入库时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入库时间")
    private LocalDateTime inTime;

    @Schema(description = "合计数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "6094")
    @ExcelProperty("合计数量")
    private BigDecimal totalCount;

    @Schema(description = "合计金额，单位：元", requiredMode = Schema.RequiredMode.REQUIRED, example = "11332")
    @ExcelProperty("合计金额，单位：元")
    private BigDecimal totalPrice;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    private List<String> fileUrl;
    /**
     * 规格
     */
    private List<String> specifications;
    @Schema(description = "创建人", example = "芋道")
    private String creator;
    @Schema(description = "创建人名称", example = "芋道")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "ERP 领料出库单项列表")
    private List<ErpPickingInRespVO.Item> items;

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

        @Schema(description = "关联批次信息名称", example = "随便")
        private String associatedBatchName;

        @Schema(description = "关联批次库存数量")
        private Integer associationBatchNum;
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
         * 还料数量
         */
        @Schema(description = "还料数量")
        private BigDecimal returnMaterialsCount;

        /**
         * 库存数量
         */
        @Schema(description = "库存数量")
        private BigDecimal stockCount;

        /**
         * 产品名称
         */
        @Schema(description = "产品名称")
        private String productName;

        /**
         * 产品编号
         */
        @Schema(description = "产品编号")
        private String productBarCode;

        /**
         * 产品单位名称
         */
        @Schema(description = "产品单位名称")
        private String productUnitName;

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
