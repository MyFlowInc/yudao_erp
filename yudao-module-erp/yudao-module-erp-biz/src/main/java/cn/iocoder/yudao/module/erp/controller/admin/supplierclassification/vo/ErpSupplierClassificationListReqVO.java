package cn.iocoder.yudao.module.erp.controller.admin.supplierclassification.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 供应商分类列表 Request VO")
@Data
public class ErpSupplierClassificationListReqVO {

    @Schema(description = "id", example = "4051")
    private Long id;

    @Schema(description = "父id", example = "11385")
    private Long parentId;

    @Schema(description = "分类名称", example = "王五")
    private String name;

    @Schema(description = "编号")
    private String code;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}