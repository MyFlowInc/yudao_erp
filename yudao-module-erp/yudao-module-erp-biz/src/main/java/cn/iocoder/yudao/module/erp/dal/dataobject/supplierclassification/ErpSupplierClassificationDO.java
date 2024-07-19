package cn.iocoder.yudao.module.erp.dal.dataobject.supplierclassification;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 供应商分类 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_supplier_classification")
@KeySequence("erp_supplier_classification_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpSupplierClassificationDO extends BaseDO {

    public static final Long PARENT_ID_ROOT = 0L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 父id
     */
    private Long parentId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 编号
     */
    private String code;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     */
    private String status;

}