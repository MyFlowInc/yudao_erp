package cn.iocoder.yudao.module.erp.dal.dataobject.costing;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 成本核算 DO
 *
 * @author 那就这样吧
 */
@TableName(value = "erp_costing", autoResultMap = true)
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
     * 类型
     */
    private Integer type;
    /**
     * 关联项目id
     */
    private Long associationProjectId;
    /**
     * 核算时间
     */
    private LocalDateTime costingTime;
    /**
     * 核算开始时间
     */
    private LocalDateTime startTime;
    /**
     * 核算结束时间
     */
    private LocalDateTime endTime;
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

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件
     */
   @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> fileUrl;

    /**
     * 规格
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> specifications;

}