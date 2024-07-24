package cn.iocoder.yudao.module.erp.controller.admin.purchase.vo.supplier;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - ERP 供应商分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ErpSupplierPageReqVO extends PageParam {

    @Schema(description = "供应商编号", example = "629")
    private Long id;

    @Schema(description = "供应商名称", example = "李四")
    private String name;

    @Schema(description = "供应商分类id")
    private Long supplierClassification;

    @Schema(description = "父级编号", example = "9216")
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

    @Schema(description = "开启状态", example = "1")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "纳税人识别号")
    private String taxNo;

    @Schema(description = "税率")
    private BigDecimal taxPercent;

    @Schema(description = "开户行", example = "芋艿")
    private String bankName;

    @Schema(description = "开户账号", example = "29722")
    private String bankAccount;

    @Schema(description = "开户地址")
    private String bankAddress;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}