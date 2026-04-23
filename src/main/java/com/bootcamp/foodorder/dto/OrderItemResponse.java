package com.bootcamp.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long menuId;
    private String menuName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
