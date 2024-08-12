package cn.iocoder.yudao.module.erp.controller.admin.material.vo.in;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 领料出库单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpPickingInPageReqVO extends PageParam {

    @Schema(description = "领料单编号", example = "21969")
    private Long id;

    @Schema(description = "领料号")
    private String no;

    @Schema(description = "供应商编号", example = "7291")
    private Long supplierId;

    @Schema(description = "关联请购单", example = "16463")
    private Long associationRequisitionId;

    @Schema(description = "关联项目", example = "16463")
    private Long associationProjectId;

    @Schema(description = "入库时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inTime;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}