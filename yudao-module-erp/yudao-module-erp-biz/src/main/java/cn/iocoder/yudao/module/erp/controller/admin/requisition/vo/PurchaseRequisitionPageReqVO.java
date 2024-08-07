package cn.iocoder.yudao.module.erp.controller.admin.requisition.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 新增请购分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PurchaseRequisitionPageReqVO extends PageParam {

    @Schema(description = "id", example = "1157")
    private Long id;

    @Schema(description = "请购编号")
    private String requisitionCode;

    @Schema(description = "请购类型", example = "2")
    private Integer requisitionType;

    @Schema(description = "关联项目", example = "2")
    private Integer associationProject;

    @Schema(description = "关联产品id", example = "2")
    private Integer productId;

    @Schema(description = "请购单时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private String[] requisitionTime;

    @Schema(description = "预计交期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private String[] estimatedTime;

    @Schema(description = "审核类型", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}