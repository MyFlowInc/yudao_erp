package cn.iocoder.yudao.module.erp.controller.admin.costing.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 成本核算分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpCostingPageReqVO extends PageParam {

    @Schema(description = "核算编号")
    private String no;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "核算单")
    private Long costId;

    @Schema(description = "类型", example = "2")
    private Integer type;

    @Schema(description = "关联项目id", example = "15076")
    private Long associationProjectId;

    @Schema(description = "核算时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] costingTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}