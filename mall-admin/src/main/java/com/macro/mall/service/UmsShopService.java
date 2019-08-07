package com.macro.mall.service;

import com.macro.mall.dto.PmsBrandParam;
import com.macro.mall.dto.UmsShopParam;
import com.macro.mall.model.PmsBrand;
import com.macro.mall.model.UmsShop;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品品牌Service
 * Created by macro on 2018/4/26.
 */
public interface UmsShopService {
    List<UmsShop> listAllShop();

    int createShop(UmsShopParam umsShopParam);
    @Transactional
    int updateShop(Long id, UmsShopParam umsShopParam);

    int deleteShop(Long id);

    int deleteShop(List<Long> ids);

    List<UmsShop> listShop(String keyword, int pageNum, int pageSize);

    UmsShop getShop(Long id);

    int updateShowStatus(List<Long> ids, Integer showStatus);

}
