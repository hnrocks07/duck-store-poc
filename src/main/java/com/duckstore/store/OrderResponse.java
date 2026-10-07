package com.duckstore.store;
import java.math.BigDecimal;
import java.util.List;
public record OrderResponse(PackageType packageType, List<String> protections, BigDecimal totalToPay, List<PricingLine> breakdown) { }
