package com.macro.mall.mapper;

import com.macro.mall.model.UmsShop;
import com.macro.mall.model.UmsShopExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsShopMapper {
    long countByExample(UmsShopExample example);

    int deleteByExample(UmsShopExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsShop record);

    int insertSelective(UmsShop record);

    List<UmsShop> selectByExample(UmsShopExample example);

    UmsShop selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UmsShop record, @Param("example") UmsShopExample example);

    int updateByExample(@Param("record") UmsShop record, @Param("example") UmsShopExample example);

    int updateByPrimaryKeySelective(UmsShop record);

    int updateByPrimaryKey(UmsShop record);
}