package cn.iocoder.yudao.module.erp.controller.admin.material.vo.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.alibaba.excel.annotation.*;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - ERP 还料入库单 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpReturnMaterialsRespVO {

    @Schema(description = "还料单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1961")
    @ExcelProperty("还料单编号")
    private Long id;

    @Schema(description = "还料单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("还料单号")
    private String no;

    @Schema(description = "供应商编号", example = "14163")
    @ExcelProperty("供应商编号")
    private Long supplierId;

    @Schema(description = "关联请购单", example = "11852")
    @ExcelProperty("关联请购单")
    private Long associationRequisitionId;

    @Schema(description = "关联请购单编号", example = "16463")
    @ExcelProperty("关联请购单编号")
    private String associationRequisitionNo;

    @Schema(description = "关联项目id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17900")
    @ExcelProperty("关联项目id")
    private Long associationProjectId;

    @Schema(description = "关联领料项id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17900")
    @ExcelProperty("关联领料项id")
    private Long associatedPickingItemId;

    @Schema(description = "关联项目名称", example = "16463")
    @ExcelProperty("关联项目名称")
    private String associationProjectName;

    @Schema(description = "入库时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("入库时间")
    private LocalDateTime inTime;

    @Schema(description = "合计数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "9208")
    @ExcelProperty("合计数量")
    private BigDecimal totalCount;

    @Schema(description = "合计金额，单位：元", requiredMode = Schema.RequiredMode.REQUIRED, example = "1443")
    @ExcelProperty("合计金额，单位：元")
    private BigDecimal totalPrice;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "附件 URL", example = "https://www.iocoder.cn")
    @ExcelProperty("附件 URL")
    private String fileUrl;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
    @Schema(description = "ERP 还料入库单项列表")
    private List<ErpReturnMaterialsRespVO.Item> items;
    @Data
    public static class Item {
        @Schema(description = "id")
        private Long id;

        @Schema(description = "还料单ID")
        private Long returnId;

        @Schema(description = "仓库id")
        private Long warehouseId;
        /**
         * 关联批次id
         */
        @Schema(description = "批次ID")
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
        private Long productId;
        /**
         * 产品名称
         */
        @Schema(description = "产品名称")
        private String productName;
        /**
         * 产品单位编号
         */
        @Schema(description = "产品单位编号")
        private Long productUnitId;
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
         * 库存数量
         */
        @Schema(description = "库存数量")
        private BigDecimal stockCount;
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