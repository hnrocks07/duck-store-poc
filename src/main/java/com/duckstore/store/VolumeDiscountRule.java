package com.duckstore.store;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class VolumeDiscountRule implements PricingRule {
  public void apply(PricingContext c) {
    if (c.quantity() > 100)
      c.add("Volume discount (20%)", c.base().multiply(new java.math.BigDecimal("-0.20")));
  }
}
