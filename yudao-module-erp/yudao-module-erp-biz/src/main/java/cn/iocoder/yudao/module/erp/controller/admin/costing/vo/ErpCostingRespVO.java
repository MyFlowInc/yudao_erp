package cn.iocoder.yudao.module.erp.controller.admin.costing.vo;

import cn.iocoder.yudao.module.erp.controller.admin.requisition.vo.PurchaseRequisitionRespVO;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 成本核算 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpCostingRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28097")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "核算编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("核算编号")
    private String no;

    @Schema(description = "状态", example = "2")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "关联项目id", requiredMode = Schema.RequiredMode.REQUIRED, example = "15076")
    @ExcelProperty("关联项目id")
    private Long associationProjectId;

    @Schema(description = "核算时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("核算时间")
    private LocalDateTime costingTime;
    /**
     * 核算开始时间
     */
    @ExcelProperty("核算开始时间")
    @Schema(description = "核算开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;
    /**
     * 核算结束时间
     */
    @ExcelProperty("核算结束时间")
    @Schema(description = "核算结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    @Schema(description = "领料量", example = "17193")
    @ExcelProperty("领料量")
    private BigDecimal pickingCount;

    @Schema(description = "领料成本")
    @ExcelProperty("领料成本")
    private BigDecimal pickingCost;

    @Schema(description = "退料量", example = "28833")
    @ExcelProperty("退料量")
    private BigDecimal returnMaterialsCount;

    @Schema(description = "退料成本")
    @ExcelProperty("退料成本")
    private BigDecimal returnMaterialsCost;

    @Schema(description = "物料成本")
    @ExcelProperty("物料成本")
    private BigDecimal materialCost;

    @Schema(description = "其他收入成本")
    @ExcelProperty("其他收入成本")
    private BigDecimal otherCost;
    /**
     * 其他支出成本
     */
    @Schema(description = "其他支出成本")
    @ExcelProperty("其他支出成本")
    private BigDecimal otherExpensesCost;

    @Schema(description = "总成本")
    @ExcelProperty("总成本")
    private BigDecimal totalCost;

    @Schema(description = "关联项目名称", example = "16463")
    @ExcelProperty("关联项目名称")
    private String associationProjectName;

    @Schema(description = "创建人", example = "芋道")
    private String creator;

    @Schema(description = "创建人名称", example = "芋道")
    private String creatorName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "请购单项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ErpCostingRespVO.Item> items;


    @Data
    public static class Item {
        /**
         * id
         */
        @ExcelIgnore
        private Long id;
        /**
         * 核算单id
         */
        @ExcelIgnore
        private Long costId;
        /**
         * 核算项名称
         */
        @ExcelProperty("核算项名称")
        @Schema(description = "核算项名称")
        private String name;
        /**
         * 核算项类型
         */
        @ExcelIgnore
        private Integer type;
        /**
         * 类型名称
         */
        @ExcelProperty("类型名称")
        @Schema(description = "类型名称")
        private String typeName;
        /**
         * 数量
         */
        @ExcelProperty("数量")
        @Schema(description = "数量")
        private BigDecimal count;
        /**
         * 单价
         */
        @ExcelProperty("单价")
        @Schema(description = "单价")
        private BigDecimal unitPrice;
        /**
         * 金额
         */
        @ExcelProperty("总金额")
        @Schema(description = "金额")
        private BigDecimal money;
        /**
         * 关联批次id
         */
        @ExcelIgnore
        private Long associatedBatchId;
        /**
         * 项目名称
         */
        @ExcelProperty("项目名称")
        @Schema(description = "项目名称")
        private String associatedProjectName;
        /**
         * 关联批次名称
         */
        @ExcelProperty("批次名称")
        @Schema(description = "批次名称")
        private String associatedBatchName;

        /**
         * 关联项目id
         */
        @ExcelIgnore
        private Long associatedProjectId;
    }
}