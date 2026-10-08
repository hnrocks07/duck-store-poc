package com.duckstore.store;

public interface PricingRule {
  void apply(PricingContext context);
}
