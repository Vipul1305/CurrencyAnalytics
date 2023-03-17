package com.APIwiz.CurrencyAnalytics.Controller;

import com.APIwiz.CurrencyAnalytics.DTO.ExchangeCurrencyRequestDto;
import com.APIwiz.CurrencyAnalytics.DTO.ExchangeCurrencyResponseDto;
import com.APIwiz.CurrencyAnalytics.DTO.PredictCurrencyRequestDto;
import com.APIwiz.CurrencyAnalytics.DTO.PredictCurrencyResponseDto;
import com.APIwiz.CurrencyAnalytics.Service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/currency")
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @GetMapping("/exchange")
    public ResponseEntity exchangeCurrency(@RequestBody ExchangeCurrencyRequestDto exchangeCurrencyRequestDto){
        ExchangeCurrencyResponseDto exchangeCurrencyResponseDto;
        try {
            exchangeCurrencyResponseDto = currencyService.exchangeCurrency(exchangeCurrencyRequestDto);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(exchangeCurrencyResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/predict")
    public ResponseEntity predictCurrency(@RequestBody PredictCurrencyRequestDto predictCurrencyRequestDto){
        PredictCurrencyResponseDto predictCurrencyResponseDto = currencyService.predictCurrency(predictCurrencyRequestDto);
        return new ResponseEntity(predictCurrencyResponseDto, HttpStatus.CREATED);
    }

    @PostConstruct
    public ResponseEntity init() {
        try {
            currencyService.fetchAndStoreCurrencyData();
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
