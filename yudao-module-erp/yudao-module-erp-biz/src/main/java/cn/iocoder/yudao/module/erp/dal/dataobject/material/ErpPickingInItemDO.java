package cn.iocoder.yudao.module.erp.dal.dataobject.material;

import io.swagger.v3.oas.annotations.media.Schema;
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
 * ERP 领料出库单项 DO
 *
 * @author 那就这样吧
 */
@TableName("erp_picking_in_item")
@KeySequence("erp_picking_in_item_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpPickingInItemDO extends BaseDO {

    /**
     * 领料项编号
     */
    @TableId
    private Long id;
    /**
     * 领料单编号
     */
    private Long inId;
    /**
     * 仓库编号
     */
    private Long warehouseId;
    /**
     * 关联批次id
     */
    private Long associatedBatchId;

    /**
     * 关联请购项
     */
    private Long associationRequisitionProductId;
    /**
     * 产品编号
     */
    private Long productId;
    /**
     * 产品单位编号
     */
    private Long productUnitId;
    /**
     * 产品单价
     */
    private BigDecimal productPrice;
    /**
     * 产品数量
     */
    private BigDecimal count;

    /**
     * 还料数量
     */
    private BigDecimal returnMaterialsCount;
    /**
     * 合计金额，单位：元
     */
    private BigDecimal totalPrice;
    /**
     * 备注
     */
    private String remark;

}