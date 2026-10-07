package com.duckstore.store;

import org.springframework.stereotype.Component;
import java.math.*;
import java.util.*;

@Component
public class PricingCalculator {
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    public Calculation calculate(BigDecimal unitPrice, int quantity, PackageType packageType, String country, ShippingMode mode) {
        BigDecimal base = unitPrice.multiply(BigDecimal.valueOf(quantity));
        List<PricingLine> lines = new ArrayList<>(List.of(new PricingLine("Merchandise", base)));
        // Documented decision: all percentage rules use merchandise base, then shipping is added.
        if (quantity > 100) addPercent(lines, "Volume discount (20%)", base, "-0.20");
        switch (packageType) {
            case Wood -> addPercent(lines, "Wood package surcharge (5%)", base, "0.05");
            case Plastic -> addPercent(lines, "Plastic package surcharge (10%)", base, "0.10");
            case Cardboard -> addPercent(lines, "Cardboard package discount (1%)", base, "-0.01");
        }
        BigDecimal destinationRate = switch (country.trim().toLowerCase(Locale.ROOT)) {
            case "usa" -> new BigDecimal("0.18"); case "bolivia" -> new BigDecimal("0.13"); case "india" -> new BigDecimal("0.19"); default -> new BigDecimal("0.15");
        };
        addPercent(lines, "Destination " + country.trim() + " adjustment (" + destinationRate.multiply(HUNDRED).stripTrailingZeros().toPlainString() + "%)", base, destinationRate.toPlainString());
        BigDecimal shipping = switch(mode) {
            case Sea -> new BigDecimal("400");
            case Land -> new BigDecimal("10").multiply(BigDecimal.valueOf(quantity));
            case Air -> new BigDecimal("30").multiply(BigDecimal.valueOf(quantity));
        };
        if (mode == ShippingMode.Air && quantity > 1000) shipping = shipping.multiply(new BigDecimal("0.85"));
        lines.add(new PricingLine(mode + " shipping", shipping));
        BigDecimal total = lines.stream().map(PricingLine::amount).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
        return new Calculation(total, lines.stream().map(l -> new PricingLine(l.label(), l.amount().setScale(2, RoundingMode.HALF_UP))).toList());
    }
    private void addPercent(List<PricingLine> lines, String label, BigDecimal base, String rate) { lines.add(new PricingLine(label, base.multiply(new BigDecimal(rate)))); }
    public record Calculation(BigDecimal total, List<PricingLine> lines) { }
}
