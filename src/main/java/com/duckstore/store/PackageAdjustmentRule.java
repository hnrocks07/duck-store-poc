package com.duckstore.store;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class PackageAdjustmentRule implements PricingRule {
    private static final Map<PackageType, BigDecimal> RATES = Map.of(
        PackageType.Wood, new BigDecimal("0.05"),
        PackageType.Plastic, new BigDecimal("0.10"),
        PackageType.Cardboard, new BigDecimal("-0.01")
    );

    @Override
    public void apply(PricingContext context) {
        BigDecimal rate = RATES.get(context.packageType());
        String label = context.packageType() == PackageType.Cardboard
            ? "Cardboard package discount (1%)"
            : context.packageType() + " package surcharge (" + rate.movePointRight(2).stripTrailingZeros() + "%)";
        context.add(label, context.base().multiply(rate));
    }
}
