package cn.iocoder.yudao.module.erp.controller.admin.product.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 产品新增/修改 Request VO")
@Data
public class ErpProductSaveReqVO {

    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16426")
    private Long id;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "产品名称不能为空")
    private String name;

    @Schema(description = "产品条码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "产品条码不能为空")
    private String barCode;

    @Schema(description = "产品分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6275")
    @NotNull(message = "产品分类编号不能为空")
    private Long categoryId;

    @Schema(description = "关联供应商Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "31572")
    @NotEmpty(message = "关联供应商Id不能为空")
    private String associationSupplierId;

    @Schema(description = "单位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "28652")
    @NotNull(message = "单位编号不能为空")
    private Integer unitId;

    @Schema(description = "产品状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "产品状态不能为空")
    private Integer status;

    @Schema(description = "产品规格")
    private String standard;

    @Schema(description = "产品备注", example = "随便")
    private String remark;

    @Schema(description = "保质期天数")
    private Integer expiryDay;

    @Schema(description = "基础重量（kg）")
    private BigDecimal weight;

    @Schema(description = "采购价格，单位：元", example = "2049")
    private BigDecimal purchasePrice;

    @Schema(description = "销售价格，单位：元", example = "24741")
    private BigDecimal salePrice;

    @Schema(description = "最低价格，单位：元", example = "22646")
    private BigDecimal minPrice;

}