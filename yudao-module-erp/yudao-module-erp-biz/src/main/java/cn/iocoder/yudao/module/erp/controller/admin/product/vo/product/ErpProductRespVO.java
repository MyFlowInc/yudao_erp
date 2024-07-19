package cn.iocoder.yudao.module.erp.controller.admin.product.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - ERP 产品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpProductRespVO {

    @Schema(description = "产品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16426")
    @ExcelProperty("产品编号")
    private Long id;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("产品名称")
    private String name;

    @Schema(description = "产品条码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品条码")
    private String barCode;

    @Schema(description = "产品分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6275")
    @ExcelProperty("产品分类编号")
    private Long categoryId;

    @Schema(description = "关联供应商Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "31572")
    @ExcelProperty("关联供应商Id")
    private String associationSupplierId;

    @Schema(description = "单位编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "28652")
    @ExcelProperty("单位编号")
    private Integer unitId;
    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "个")
    @ExcelProperty("单位")
    private String unitName;

    @Schema(description = "产品状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("产品状态")
    private Integer status;

    @Schema(description = "产品规格")
    @ExcelProperty("产品规格")
    private String standard;

    @Schema(description = "产品备注", example = "随便")
    @ExcelProperty("产品备注")
    private String remark;

    @Schema(description = "保质期天数")
    @ExcelProperty("保质期天数")
    private Integer expiryDay;

    @Schema(description = "基础重量（kg）")
    @ExcelProperty("基础重量（kg）")
    private BigDecimal weight;

    @Schema(description = "采购价格，单位：元", example = "2049")
    @ExcelProperty("采购价格，单位：元")
    private BigDecimal purchasePrice;

    @Schema(description = "销售价格，单位：元", example = "24741")
    @ExcelProperty("销售价格，单位：元")
    private BigDecimal salePrice;

    @Schema(description = "最低价格，单位：元", example = "22646")
    @ExcelProperty("最低价格，单位：元")
    private BigDecimal minPrice;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}