package cn.iocoder.yudao.module.erp.controller.admin.supplierclassification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;

@Schema(description = "管理后台 - 供应商分类新增/修改 Request VO")
@Data
public class ErpSupplierClassificationSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4051")
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

}