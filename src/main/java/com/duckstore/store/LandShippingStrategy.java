package com.duckstore.store;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LandShippingStrategy implements ShippingStrategy {
    public ShippingMode mode() { return ShippingMode.Land; }
    public BigDecimal cost(int quantity) { return BigDecimal.TEN.multiply(BigDecimal.valueOf(quantity)); }
    public List<String> protections(PackageType packageType) { return List.of("Polystyrene balls"); }
}
