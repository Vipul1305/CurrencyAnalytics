package com.APIwiz.CurrencyAnalytics.Service;

import com.APIwiz.CurrencyAnalytics.DTO.ExchangeCurrencyRequestDto;
import com.APIwiz.CurrencyAnalytics.DTO.ExchangeCurrencyResponseDto;
import com.APIwiz.CurrencyAnalytics.DTO.PredictCurrencyRequestDto;
import com.APIwiz.CurrencyAnalytics.DTO.PredictCurrencyResponseDto;
import com.APIwiz.CurrencyAnalytics.Entity.Currency;
import com.APIwiz.CurrencyAnalytics.Enum.G10Currency;
import com.APIwiz.CurrencyAnalytics.Repository.CurrencyRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {

    @Autowired
    CurrencyRepository currencyRepository;

    public ExchangeCurrencyResponseDto exchangeCurrency(ExchangeCurrencyRequestDto exchangeCurrencyRequestDto) throws Exception{
        String s = exchangeCurrencyRequestDto.getBase();
        String[] st = s.split(" +");
        String code_1 = st[1]; // base currency code
        String code_2 = exchangeCurrencyRequestDto.getDestination();
        //check for G10Currency
        try {
            G10Currency.valueOf(code_1);
            G10Currency.valueOf(code_2);
        }catch (Exception e){
            throw new Exception("G10 Currency Supported Only!!");
        }
        Currency data = currencyRepository.getAllCurrencyCode(code_1);
        // USD to base currency rate => 1 / 0.94(EUR comp to USD) =>  1.06
        double exchangeRate = 1 / data.getExchangeRate();

        Currency base = currencyRepository.getAllCurrencyCode(code_2);
        double baseAmount = base.getExchangeRate();

        // 2EUR to INR => (1.06 * 2) * 84;
        double convertedAmount = (exchangeRate * Integer.parseInt(st[0])) * baseAmount;

        //Response
        ExchangeCurrencyResponseDto response = new ExchangeCurrencyResponseDto();
        response.setBase(st[0] + code_1);
        response.setDestination(String.valueOf(Math.round(convertedAmount*100d)/100d) + " " + code_2);

        return response;

    }

    public PredictCurrencyResponseDto predictCurrency(PredictCurrencyRequestDto predictCurrencyRequestDto){
        List<Currency> data = currencyRepository.findByAllCode(predictCurrencyRequestDto.getBaseCurrency());

        double[] x = data.stream().mapToDouble(d -> d.getDate().toEpochDay()).toArray();
        double[] y = data.stream().mapToDouble(Currency::getExchangeRate).toArray();
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i], y[i]);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(predictCurrencyRequestDto.getDate(), formatter);

        double predictedValue = regression.predict(date.toEpochDay());
        //Response
        PredictCurrencyResponseDto predictCurrencyResponseDto = new PredictCurrencyResponseDto();
        predictCurrencyResponseDto.setPredictedValue(Math.round(predictedValue*100d)/100d);
        return predictCurrencyResponseDto;
    }

    // Got all the currencies values from open currency API for the past 30 days
    public void fetchAndStoreCurrencyData() throws Exception{
        OkHttpClient httpClient = new OkHttpClient().newBuilder().build();

        Request request = new Request.Builder()
                .url("https://api.apilayer.com/exchangerates_data/timeseries?start_date=2023-02-15&end_date=2023-03-16&base=USD")
                .addHeader("apikey", "aNbccarkCeSHTvhfqSKBvqgVLCIfwMCk")
            .build();
        try {
            Response response = httpClient.newCall(request).execute();
            String json = response.body().string();
            // parse the JSON and store the currency data in the database
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode ratesNode = rootNode.get("rates");
            for (Iterator<Map.Entry<String, JsonNode>> it = ratesNode.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                LocalDate date = LocalDate.parse(entry.getKey());
                JsonNode rates = entry.getValue();
                for (Iterator<Map.Entry<String, JsonNode>> it2 = rates.fields(); it2.hasNext(); ) {
                    Map.Entry<String, JsonNode> entry2 = it2.next();
                    String currencyCode = entry2.getKey();
                    double exchangeRate = entry2.getValue().decimalValue().doubleValue();
                    Currency currency = new Currency(currencyCode, exchangeRate, date);
                    currencyRepository.save(currency);
                }
            }
        }catch (Exception e){
            throw new Exception();
        }

    }
}
