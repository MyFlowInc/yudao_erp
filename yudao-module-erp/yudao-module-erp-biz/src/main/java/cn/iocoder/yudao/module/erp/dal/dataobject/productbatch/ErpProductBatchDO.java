package cn.iocoder.yudao.module.erp.dal.dataobject.productbatch;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * ERP产品批次信息 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_product_batch")
@KeySequence("erp_product_batch_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpProductBatchDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 批次名称
     */
    private String name;
    /**
     * 批次类型
     */
    private String type;
    /**
     * 关联产品
     */
    private Long associationProductId;

    /**
     * 产品单价
     */
    private BigDecimal unitPrice;
    /**
     * 批次编号
     */
    private String code;
    /**
     * 批次入库数量
     */
    private BigDecimal inventoryQuantity;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态
     */
    private Integer status;

}