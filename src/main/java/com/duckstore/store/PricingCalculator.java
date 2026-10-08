package com.duckstore.store;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class PricingCalculator {
  private final List<PricingRule> rules;

  public PricingCalculator(List<PricingRule> rules) {
    this.rules = rules;
  }

  public Calculation calculate(
      BigDecimal unitPrice,
      int quantity,
      PackageType packageType,
      String country,
      ShippingMode mode) {
    PricingContext context = new PricingContext(unitPrice, quantity, packageType, country, mode);
    rules.forEach(rule -> rule.apply(context));
    List<PricingLine> lines = context.lines();
    BigDecimal total =
        lines.stream()
            .map(PricingLine::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    return new Calculation(total, lines);
  }

  public record Calculation(BigDecimal total, List<PricingLine> lines) {}
}
