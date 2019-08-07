package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.PmsBrandParam;
import com.macro.mall.dto.UmsShopParam;
import com.macro.mall.mapper.PmsBrandMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.UmsShopMapper;
import com.macro.mall.model.*;

import com.macro.mall.service.UmsAdminService;
import com.macro.mall.service.UmsShopService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品品牌Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsShopServiceImpl implements UmsShopService {
    @Autowired
    private UmsShopMapper umsShopMapper;
    @Autowired
    private UmsAdminService umsAdminService;

    @Override
    public List<UmsShop> listAllShop() {
        Long umsShopId = umsAdminService.getCurrentUmsAdmin().getShopId();
        UmsShopExample umsShopExample = new UmsShopExample();
        if(umsShopId!=0){
            umsShopExample.createCriteria().andIdEqualTo(umsShopId);
        }
        return umsShopMapper.selectByExample(umsShopExample);

    }

    @Override
    public int createShop(UmsShopParam umsShopParam) {
        UmsShop umsShop = new UmsShop();
        BeanUtils.copyProperties(umsShopParam, umsShop);

        return umsShopMapper.insertSelective(umsShop);
    }

    @Override
    public int updateShop(Long id, UmsShopParam umsShopParam) {
        UmsShop umsShop = new UmsShop();
        BeanUtils.copyProperties(umsShopParam, umsShop);
        umsShop.setId(id);
        return umsShopMapper.updateByPrimaryKeySelective(umsShop);
    }

    @Override
    public int deleteShop(Long id) {
        return umsShopMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteShop(List<Long> ids) {
        UmsShopExample umsShopExample = new UmsShopExample();
        umsShopExample.createCriteria().andIdIn(ids);
        return umsShopMapper.deleteByExample(umsShopExample);
    }

    @Override
    public List<UmsShop> listShop(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Long umsShopId = umsAdminService.getCurrentUmsAdmin().getShopId();
        UmsShopExample umsShopExample = new UmsShopExample();
        UmsShopExample.Criteria criteria = umsShopExample.createCriteria();
        if(umsShopId!=0){
            criteria.andIdEqualTo(umsShopId);
        }


        if (!StringUtils.isEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        return umsShopMapper.selectByExample(umsShopExample);
    }

    @Override
    public UmsShop getShop(Long id) {
        return umsShopMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        UmsShop umsShop = new UmsShop();
        umsShop.setStatus(showStatus);
        UmsShopExample umsShopExample = new UmsShopExample();
        umsShopExample.createCriteria().andIdIn(ids);
        return umsShopMapper.updateByExampleSelective(umsShop, umsShopExample);
    }


}
