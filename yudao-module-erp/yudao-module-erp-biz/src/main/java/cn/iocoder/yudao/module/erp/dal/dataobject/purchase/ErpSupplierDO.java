package cn.iocoder.yudao.module.erp.dal.dataobject.purchase;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * ERP 供应商 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_supplier")
@KeySequence("erp_supplier_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpSupplierDO extends BaseDO {

    /**
     * 供应商编号
     */
    @TableId
    private Long id;
    /**
     * 供应商名称
     */
    private String name;
    /**
     * 供应商分类id
     */
    private String supplierClassification;
    /**
     * 父级编号
     */
    private String parentId;
    /**
     * 评级
     */
    private String grade;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 传真
     */
    private String fax;
    /**
     * 备注
     */
    private String remark;
    /**
     * 开启状态
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 纳税人识别号
     */
    private String taxNo;
    /**
     * 税率
     */
    private BigDecimal taxPercent;
    /**
     * 开户行
     */
    private String bankName;
    /**
     * 开户账号
     */
    private String bankAccount;
    /**
     * 开户地址
     */
    private String bankAddress;

}