package cn.iocoder.yudao.module.erp.controller.admin.material.vo.out;


import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.erp.dal.dataobject.material.ErpReturnMaterialsDO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - ERP 还料入库单新增/修改 Request VO")
@Data
public class ErpReturnMaterialsSaveReqVO {

    @Schema(description = "还料单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1961")
    private Long id;

    @Schema(description = "还料单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String no;

    @Schema(description = "供应商编号", example = "14163")
    private Long supplierId;

    @Schema(description = "关联请购单", example = "11852")
    private Long associationRequisitionId;

    @Schema(description = "关联项目id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17900")
    @NotNull(message = "关联项目id不能为空")
    private Long associationProjectId;

    @Schema(description = "入库时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "入库时间不能为空")
    private LocalDateTime inTime;

    @Schema(description = "合计数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "9208")
    @NotNull(message = "合计数量不能为空")
    private BigDecimal totalCount;

    @Schema(description = "合计金额，单位：元", requiredMode = Schema.RequiredMode.REQUIRED, example = "1443")
    @NotNull(message = "合计金额，单位：元不能为空")
    private BigDecimal totalPrice;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "附件 URL", example = "https://www.iocoder.cn")
    private String fileUrl;

    @Schema(description = "ERP 还料入库单项列表")
    private List<ErpReturnMaterialsSaveReqVO.Item> items;

    @Data
    public static class Item {
        @Schema(description = "id")
        private Long id;

        private Long returnId;

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

        @Schema(description = "关联领料项id", requiredMode = Schema.RequiredMode.REQUIRED, example = "17900")
        @NotNull(message = "领料项不能为空")
        private Long associatedPickingItemId;
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