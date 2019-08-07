package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsCartItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderCartParam {
    OrderParam orderParam;
    List<OmsCartItem> cartItemList;

}
