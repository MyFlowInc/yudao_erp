package cn.iocoder.yudao.module.erp.controller.admin.costing.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.erp.dal.dataobject.costing.ErpCostItemDO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 成本核算新增/修改 Request VO")
@Data
public class ErpCostingSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28097")
    private Long id;

    @Schema(description = "核算编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String no;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "关联项目id", requiredMode = Schema.RequiredMode.REQUIRED, example = "15076")
    @NotNull(message = "关联项目id不能为空")
    private Long associationProjectId;

    @Schema(description = "核算时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "核算时间不能为空")
    private LocalDateTime costingTime;

    @Schema(description = "领料量", example = "17193")
    private BigDecimal pickingCount;

    @Schema(description = "领料成本")
    private BigDecimal pickingCost;

    @Schema(description = "退料量", example = "28833")
    private BigDecimal returnMaterialsCount;

    @Schema(description = "退料成本")
    private BigDecimal returnMaterialsCost;

    @Schema(description = "物料成本")
    private BigDecimal materialCost;

    @Schema(description = "其他成本")
    private BigDecimal otherCost;

    @Schema(description = "总成本")
    private BigDecimal totalCost;

//    @Schema(description = "成本核算项列表")
//    private List<ErpCostItemDO> costItems;

    @Schema(description = "请购单项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ErpCostingSaveReqVO.Item> items;

    @Data
    public static class Item {
        /**
         * 核算单id
         */
        @Schema(description = "核算单id")
        private Long costId;
        /**
         * 核算项名称
         */
        @Schema(description = "核算项名称")
        private String name;
        /**
         * 类型
         */
        @Schema(description = "类型")
        private String type;
        /**
         * 数量
         */
        @Schema(description = "数量")
        private BigDecimal count;
        /**
         * 单价
         */
        @Schema(description = "单价")
        private BigDecimal unitPrice;
        /**
         * 金额
         */
        @Schema(description = "金额")
        private BigDecimal money;
        /**
         * 关联批次id
         */
        @Schema(description = "关联批次id")
        private Long associatedBatchId;
        /**
         * 关联项目id
         */
        @Schema(description = "关联项目id")
        private Long associatedProjectId;
    }
}