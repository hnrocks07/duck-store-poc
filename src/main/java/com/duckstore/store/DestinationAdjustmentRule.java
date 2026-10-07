package com.duckstore.store;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class DestinationAdjustmentRule implements PricingRule {
    private static final BigDecimal DEFAULT_RATE = new BigDecimal("0.15");
    private static final Map<String, BigDecimal> RATES = Map.of("usa", new BigDecimal("0.18"), "bolivia", new BigDecimal("0.13"), "india", new BigDecimal("0.19"));

    @Override
    public void apply(PricingContext context) {
        String country = context.country().trim();
        BigDecimal rate = RATES.getOrDefault(country.toLowerCase(Locale.ROOT), DEFAULT_RATE);
        context.add("Destination " + country + " adjustment (" + rate.movePointRight(2).stripTrailingZeros() + "%)", context.base().multiply(rate));
    }
}
