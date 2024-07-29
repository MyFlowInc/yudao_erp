package cn.iocoder.yudao.module.erp.controller.admin.requisition.vo;

import cn.iocoder.yudao.module.erp.dal.dataobject.requisition.RequisitionProductDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

import javax.validation.constraints.NotNull;

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
    private Integer requisitionType;

    @Schema(description = "请购单时间")
    @ExcelProperty("请购单时间")
    private LocalDateTime requisitionTime;

    @Schema(description = "预计交期")
    @ExcelProperty("预计交期")
    private LocalDateTime estimatedTime;

    @Schema(description = "关联项目")
    @ExcelProperty("关联项目")
    private Long associationProject;

    @Schema(description = "关联项目名称")
    @ExcelProperty("关联项目名称")
    private String projectName;

    @Schema(description = "附件")
    @ExcelProperty("附件")
    private String annex;

    @Schema(description = "审核类型", example = "2")
    @ExcelProperty("审核类型")
    private Integer status;

    @Schema(description = "审核类型", example = "2")
    @ExcelProperty("开启关闭")
    private Integer open;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建人名称")
    @ExcelProperty("创建人名称")
    private String creatorName;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "请购单项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PurchaseRequisitionRespVO.Item> items;

    @Data
    public static class Item {

        @Schema(description = "请购单项编号", example = "11756")
        private Long id;

        @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "3113")
        private Long productId;

        @Schema(description = "产品数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
        @NotNull(message = "产品数量不能为空")
        private BigDecimal count;
        @Schema(description = "状态", example = "随便")
        private String status;
        @Schema(description = "是否已被选中", example = "随便")
        private String selected;
        @Schema(description = "备注", example = "随便")
        private String remark;

        // ========== 关联字段 ==========

        @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "巧克力")
        private String productName;
        @Schema(description = "产品条码", requiredMode = Schema.RequiredMode.REQUIRED, example = "A9985")
        private String productBarCode;
        @Schema(description = "产品单位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "盒")
        private String productUnitName;

        @Schema(description = "库存数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
        private BigDecimal stockCount;

        @Schema(description = "已采购数量")
        private BigDecimal purchasedNum; // 该字段仅仅在“详情”和“编辑”时使用
    }
}