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
public class CheckoutResponse {

    private Long orderId;
    private BigDecimal totalAmount;
    private String status;
    private String message;
}
