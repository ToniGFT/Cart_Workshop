package com.gftworkshop.cartMicroservice.api.dto;




public class Country {
    private Long id;
    private String name;
    private Double tax;
    private String prefix;
    private String timeZone;

    public Country() {
    }

    public Country(Long id, String name, Double tax, String prefix, String timeZone) {
        this.id = id;
        this.name = name;
        this.tax = tax;
        this.prefix = prefix;
        this.timeZone = timeZone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
