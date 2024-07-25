package cn.iocoder.yudao.module.erp.dal.dataobject.project;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("spl_project")
@Data
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpAiluoProjectDO {
    /**
     * 项目Id
     */
    @TableId
    private Long id;
    /**
     * 项目名称
     */
    private String name;
    /**
     * 状态
     */
    private String status;
}
