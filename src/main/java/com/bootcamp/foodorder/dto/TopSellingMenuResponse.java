package com.bootcamp.foodorder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TopSellingMenuResponse {

    private Long menuId;
    private String menuName;
    private Integer totalSold;
}
