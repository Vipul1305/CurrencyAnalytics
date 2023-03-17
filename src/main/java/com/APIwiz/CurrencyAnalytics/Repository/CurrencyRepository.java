package com.APIwiz.CurrencyAnalytics.Repository;

import com.APIwiz.CurrencyAnalytics.Entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency,Integer> {
    @Query(value = "select * from currency_data c where c.currency_code=:currencyCode order by date desc limit 1",
            nativeQuery = true)
    Currency getAllCurrencyCode(String currencyCode);

    @Query(value = "select * from currency_data c where c.currency_code=:code",nativeQuery = true)
    List<Currency> findByAllCode(String code);
}
