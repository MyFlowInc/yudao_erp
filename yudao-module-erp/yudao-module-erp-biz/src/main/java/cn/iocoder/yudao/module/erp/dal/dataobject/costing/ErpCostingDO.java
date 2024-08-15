package cn.iocoder.yudao.module.erp.dal.dataobject.costing;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 成本核算 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_costing")
@KeySequence("erp_costing_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpCostingDO extends BaseDO {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 核算编号
     */
    private String no;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 关联项目id
     */
    private Long associationProjectId;
    /**
     * 核算时间
     */
    private LocalDateTime costingTime;
    /**
     * 领料量
     */
    private BigDecimal pickingCount;
    /**
     * 领料成本
     */
    private BigDecimal pickingCost;
    /**
     * 退料量
     */
    private BigDecimal returnMaterialsCount;
    /**
     * 退料成本
     */
    private BigDecimal returnMaterialsCost;
    /**
     * 物料成本
     */
    private BigDecimal materialCost;
    /**
     * 其他收入成本
     */
    private BigDecimal otherCost;
    /**
     * 其他支出成本
     */
    private BigDecimal otherExpensesCost;

    /**
     * 总成本
     */
    private BigDecimal totalCost;

}