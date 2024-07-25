package cn.iocoder.yudao.module.erp.controller.admin.supplierclassification.vo;

import cn.iocoder.yudao.module.erp.dal.dataobject.supplierclassification.ErpSupplierClassificationDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 供应商分类 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpSupplierClassificationRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4051")
    @ExcelProperty("id")
    private Long id;

    @Schema(description = "父id", example = "11385")
    @ExcelProperty("父id")
    private Long parentId;

    @Schema(description = "分类名称", example = "王五")
    @ExcelProperty("分类名称")
    private String name;

    @Schema(description = "编号")
    @ExcelProperty("编号")
    private String code;

    @Schema(description = "排序")
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "状态", example = "2")
    @ExcelProperty("状态")
    private Integer status;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}