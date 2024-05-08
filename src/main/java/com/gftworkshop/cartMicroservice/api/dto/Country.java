package com.gftworkshop.cartMicroservice.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Country {
    private Long id;
    private String name;
    private Double tax;
    private String prefix;
    private String timeZone;

}
