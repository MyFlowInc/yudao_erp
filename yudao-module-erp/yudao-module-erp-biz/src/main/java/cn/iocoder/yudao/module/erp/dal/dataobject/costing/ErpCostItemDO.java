package cn.iocoder.yudao.module.erp.dal.dataobject.costing;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 成本核算项 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_cost_item")
@KeySequence("erp_cost_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpCostItemDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 核算单id
     */
    private Long costId;
    /**
     * 核算项名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 数量
     */
    private BigDecimal count;
    /**
     * 单价
     */
    private BigDecimal unitPrice;
    /**
     * 金额
     */
    private BigDecimal money;
    /**
     * 关联批次id
     */
    private Long associatedBatchId;
    /**
     * 关联项目id
     */
    private Long associatedProjectId;

}