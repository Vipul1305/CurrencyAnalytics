package com.APIwiz.CurrencyAnalytics.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "currency_data")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String currencyCode;

    private double exchangeRate;

    private LocalDate date;

    public Currency(String currencyCode, double exchangeRate, LocalDate date) {
        this.currencyCode=currencyCode;
        this.exchangeRate=exchangeRate;
        this.date=date;
    }
}
