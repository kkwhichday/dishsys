package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OmsMemberOrder extends OmsOrder {

    private String umsShopUrl;
    private String umsShopName;
    private Integer umsShopOpen;
    private String umsShopInfo;

    private List<OmsOrderItem> orderItemList;
}
