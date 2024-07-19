package cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 供应商新增/修改 Request VO")
@Data
public class ErpSupplierSaveReqVO {

    @Schema(description = "供应商编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10473")
    private Long id;

    @Schema(description = "供应商名称", example = "赵六")
    private String name;

    @Schema(description = "供应商类型", example = "2")
    private String type;

    @Schema(description = "父级编号", example = "28468")
    private String parentId;

    @Schema(description = "评级")
    private String grade;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "联系电话")
    private String telephone;

    @Schema(description = "电子邮箱")
    private String email;

    @Schema(description = "传真")
    private String fax;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "开启状态不能为空")
    private Integer status;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "纳税人识别号")
    private String taxNo;

    @Schema(description = "税率")
    private BigDecimal taxPercent;

    @Schema(description = "开户行", example = "赵六")
    private String bankName;

    @Schema(description = "开户账号", example = "11287")
    private String bankAccount;

    @Schema(description = "开户地址")
    private String bankAddress;

}