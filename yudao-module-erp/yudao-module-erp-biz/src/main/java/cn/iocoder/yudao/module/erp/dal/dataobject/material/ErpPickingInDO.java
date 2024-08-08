package cn.iocoder.yudao.module.erp.dal.dataobject.material;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * ERP 领料出库单 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_picking_in")
@KeySequence("erp_picking_in_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpPickingInDO extends BaseDO {

    /**
     * 领料单编号
     */
    @TableId
    private Long id;
    /**
     * 领料号
     */
    private String no;
    /**
     * 供应商编号
     */
    private Long supplierId;
    /**
     * 关联请购单
     */
    private Long associationRequisitionId;
    /**
     * 关联项目
     */
    private Long associationProjectId;
    /**
     * 入库时间
     */
    private LocalDateTime inTime;
    /**
     * 合计数量
     */
    private BigDecimal totalCount;
    /**
     * 合计金额，单位：元
     */
    private BigDecimal totalPrice;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 附件 URL
     */
    private String fileUrl;

}