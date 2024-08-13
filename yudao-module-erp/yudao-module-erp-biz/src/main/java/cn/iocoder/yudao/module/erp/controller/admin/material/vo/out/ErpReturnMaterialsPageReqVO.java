package cn.iocoder.yudao.module.erp.controller.admin.material.vo.out;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 还料入库单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpReturnMaterialsPageReqVO extends PageParam {

    @Schema(description = "还料单编号", example = "1961")
    private Long id;

    @Schema(description = "还料单号")
    private String no;

    @Schema(description = "创建人", example = "7291")
    private Long creatorName;

    @Schema(description = "关联请购单", example = "11852")
    private Long associationRequisitionId;

    @Schema(description = "关联项目id", example = "17900")
    private Long associationProjectId;

    @Schema(description = "关联还料单No", example = "17900")
    private String associationPickingNo;

    @Schema(description = "关联产品id", example = "2")
    private Integer productId;

    @Schema(description = "入库时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] inTime;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}