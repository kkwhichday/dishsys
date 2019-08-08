package com.macro.mall.dto;

import lombok.Data;

@Data
public class SumByItemResult {
    Long productId;
    String productName;
    Long quantity;
}
