package com.gftworkshop.cartMicroservice.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Country {
    private Long id;
    private String name;
    private Double tax;
    private String prefix;
    private String timeZone;


}
