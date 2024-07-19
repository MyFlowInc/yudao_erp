package cn.iocoder.yudao.module.erp.controller.admin.product.vo.product;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 产品分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpProductPageReqVO extends PageParam {

    @Schema(description = "产品编号", example = "16426")
    private Long id;

    @Schema(description = "产品名称", example = "张三")
    private String name;

    @Schema(description = "产品条码")
    private String barCode;

    @Schema(description = "产品分类编号", example = "6275")
    private Long categoryId;

    @Schema(description = "关联供应商Id", example = "31572")
    private String associationSupplierId;

    @Schema(description = "单位编号", example = "28652")
    private Long unitId;

    @Schema(description = "产品状态", example = "2")
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

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}