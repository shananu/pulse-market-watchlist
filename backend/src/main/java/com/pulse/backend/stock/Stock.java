package com.pulse.backend.stock;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_master")
public class Stock {

    @Id
    @Column(length = 20)
    private String symbol;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    private String sector;

    private String exchange;

    @Column(nullable = false)
    private boolean active = true;

    public Stock() {
    }

    public Stock(String symbol, String companyName, String sector, String exchange) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.exchange = exchange;
        this.active = true;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getSector() {
        return sector;
    }

    public String getExchange() {
        return exchange;
    }

    public boolean isActive() {
        return active;
    }
}