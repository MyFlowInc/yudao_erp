package cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - ERP 供应商 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ErpSupplierRespVO {

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10473")
    @ExcelProperty("供应商编号")
    private Long id;

    @Schema(description = "供应商名称", example = "赵六")
    @ExcelProperty("供应商名称")
    private String name;

    @Schema(description = "供应商类型", example = "2")
    @ExcelProperty("供应商类型")
    private String type;

    @Schema(description = "父级编号", example = "28468")
    @ExcelProperty("父级编号")
    private String parentId;

    @Schema(description = "评级")
    @ExcelProperty("评级")
    private String grade;

    @Schema(description = "联系人")
    @ExcelProperty("联系人")
    private String contact;

    @Schema(description = "手机号码")
    @ExcelProperty("手机号码")
    private String mobile;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String telephone;

    @Schema(description = "电子邮箱")
    @ExcelProperty("电子邮箱")
    private String email;

    @Schema(description = "传真")
    @ExcelProperty("传真")
    private String fax;

    @Schema(description = "备注", example = "你说的对")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("开启状态")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sort;

    @Schema(description = "纳税人识别号")
    @ExcelProperty("纳税人识别号")
    private String taxNo;

    @Schema(description = "税率")
    @ExcelProperty("税率")
    private BigDecimal taxPercent;

    @Schema(description = "开户行", example = "赵六")
    @ExcelProperty("开户行")
    private String bankName;

    @Schema(description = "开户账号", example = "11287")
    @ExcelProperty("开户账号")
    private String bankAccount;

    @Schema(description = "开户地址")
    @ExcelProperty("开户地址")
    private String bankAddress;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}