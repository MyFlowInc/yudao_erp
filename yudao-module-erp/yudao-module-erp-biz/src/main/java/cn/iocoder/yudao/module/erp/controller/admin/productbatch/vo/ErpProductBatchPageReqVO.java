package cn.iocoder.yudao.module.erp.controller.admin.productbatch.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP产品批次信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpProductBatchPageReqVO extends PageParam {

    @Schema(description = "id", example = "8702")
    private Long id;

    @Schema(description = "批次名称", example = "芋艿")
    private String name;

    @Schema(description = "批次类型", example = "2")
    private String type;

    @Schema(description = "关联产品", example = "7724")
    private Long associationProductId;

    @Schema(description = "产品单价", example = "4601")
    private BigDecimal unitPrice;

    @Schema(description = "批次编号")
    private String code;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}