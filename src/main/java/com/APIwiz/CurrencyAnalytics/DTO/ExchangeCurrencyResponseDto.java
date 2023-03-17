package com.APIwiz.CurrencyAnalytics.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExchangeCurrencyResponseDto {

    private String base;
    private String destination;

}