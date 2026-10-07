package com.duckstore.store;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AirShippingStrategy implements ShippingStrategy {
    public ShippingMode mode() { return ShippingMode.Air; }
    public BigDecimal cost(int quantity) { return new BigDecimal("30").multiply(BigDecimal.valueOf(quantity)); }
    public List<String> protections(PackageType packageType) {
        return packageType == PackageType.Plastic ? List.of("Bubble-wrap bags") : List.of("Polystyrene balls");
    }
}
