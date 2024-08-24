package cn.iocoder.yudao.module.erp.service.productbatch;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import cn.iocoder.yudao.module.erp.dal.dataobject.product.ErpProductDO;
import cn.iocoder.yudao.module.erp.dal.mysql.product.ErpProductMapper;
import cn.iocoder.yudao.module.erp.dal.redis.no.ErpNoRedisDAO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.iocoder.yudao.module.erp.controller.admin.productbatch.vo.*;
import cn.iocoder.yudao.module.erp.dal.dataobject.productbatch.ErpProductBatchDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.erp.dal.mysql.productbatch.ErpProductBatchMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.erp.enums.ErrorCodeConstants.*;
import static com.fhs.common.constant.Constant.*;

/**
 * ERP产品批次信息 Service 实现类
 *
 * @author 那就这样吧
 */
@Service
@Validated
public class ErpProductBatchServiceImpl implements ErpProductBatchService {

    @Resource
    private ErpProductBatchMapper productBatchMapper;
    @Resource
    private ErpProductMapper productMapper;
    @Resource
    private ErpNoRedisDAO noRedisDAO;


    @Override
    public Long createProductBatch(ErpProductBatchSaveReqVO createReqVO) {
        //检查新增时是否选中关联产品
        validateProductAssociationProductIdExists(createReqVO.getAssociationProductId());
        // 插入
        ErpProductBatchDO productBatch = BeanUtils.toBean(createReqVO, ErpProductBatchDO.class);
        ErpProductDO erpProductDO = productMapper.selectById(productBatch.getAssociationProductId());
        //获取同类型产品得批次信息，并进行后缀拼接
        Integer i = batchProduct(productBatch);
        int num = i + 1;
        productBatch.setName(erpProductDO.getName()+"-"+"批次"+"-"+num);
        // 1.4 生成编号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.PRODUCT_BATCH_NO_PREFIX);
        if (productBatchMapper.selectPage(new ErpProductBatchPageReqVO().setCode(no)) != null) {
            throw exception(PURCHASE_ORDER_NO_EXISTS);
        }
        productBatch.setCode(no);
        productBatchMapper.insert(productBatch);
        // 返回
        return productBatch.getId();
    }

    @Override
    public Long createProductBatchDO(ErpProductBatchDO createDO) {
        //检查新增时是否选中关联产品
        validateProductAssociationProductIdExists(createDO.getAssociationProductId());
        // 插入
        ErpProductDO erpProductDO = productMapper.selectById(createDO.getAssociationProductId());
        //获取同类型产品得批次信息，并进行后缀拼接
        Integer i = batchProduct(createDO);
        int num = i + 1;
        createDO.setName(erpProductDO.getName()+"-"+"批次"+"-"+num);
        // 1.4 生成编号，并校验唯一性
        String no = noRedisDAO.generate(ErpNoRedisDAO.PRODUCT_BATCH_NO_PREFIX);
        System.out.println(no);
        if (productBatchMapper.selectByNo(no) != null) {
            throw exception(PURCHASE_ORDER_NO_EXISTS);
        }
        createDO.setCode(no);
        productBatchMapper.insert(createDO);
        // 返回
        return createDO.getId();
    }

    @Override
    public void updateProductBatch(ErpProductBatchSaveReqVO updateReqVO) {
        // 校验存在
        validateProductBatchExists(updateReqVO.getId());
        // 更新
        ErpProductBatchDO updateObj = BeanUtils.toBean(updateReqVO, ErpProductBatchDO.class);
        productBatchMapper.updateById(updateObj);
    }


    @Override
    public void deleteProductBatch(Long id) {
        // 校验存在
        validateProductBatchExists(id);
        // 删除
        productBatchMapper.deleteById(id);
    }
    //检查批次信息是否存在
    private void validateProductBatchExists(Long id) {
        if (productBatchMapper.selectById(id) == null) {
            throw exception(PRODUCT_BATCH_NOT_EXISTS);
        }
    }
    //检查新增时是否选中批次
    private void validateProductAssociationProductIdExists(Long id) {
        if (id == null) {
            throw exception(PRODUCT_BATCH_NOT_EXISTS);
        }
    }
    public Integer batchProduct(ErpProductBatchDO productBatch) {
        //查询同类型产品批次数量
        QueryWrapper<ErpProductBatchDO> erpProductBatchDO = new QueryWrapper<ErpProductBatchDO>()
                .eq("association_product_id",productBatch.getAssociationProductId() )
                .ne("deleted",true);
        //选中后判断批次号
        if (productBatchMapper.selectList(erpProductBatchDO).isEmpty()){
            return ZERO;
        }
        return getMaxSuffix(productBatchMapper.selectList(erpProductBatchDO));
    }

    public Integer getMaxSuffix(List<ErpProductBatchDO> proBatchlist) {
        Pattern pattern = Pattern.compile("批次-(\\d+)");
        int maxSuffix = Integer.MIN_VALUE;
        for (ErpProductBatchDO obj : proBatchlist) {
            Matcher matcher = pattern.matcher(obj.getName());
            if (matcher.find()) {
                String numberStr = matcher.group(1);
                int number = Integer.parseInt(numberStr);
                if (number > maxSuffix) {
                    maxSuffix = number;
                }
            } else {
                System.out.println("未找到匹配的批次号：" + obj.getName());
            }
        }
        return maxSuffix;
    }
    @Override
    public ErpProductBatchDO getProductBatch(Long id) {
        return productBatchMapper.selectById(id);
    }

    @Override
    public PageResult<ErpProductBatchDO> getProductBatchPage(ErpProductBatchPageReqVO pageReqVO) {
        return productBatchMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ErpProductBatchDO> getProductBatchList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return productBatchMapper.selectBatchIds(ids);
    }

    @Override
    public Map<Long, ErpProductBatchDO> getProductBatchMap(Collection<Long> ids) {
        return ErpProductBatchService.super.getProductBatchMap(ids);
    }


}