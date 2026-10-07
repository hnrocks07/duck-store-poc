package com.duckstore.store;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SeaShippingStrategy implements ShippingStrategy {
    public ShippingMode mode() { return ShippingMode.Sea; }
    public BigDecimal cost(int quantity) { return new BigDecimal("400"); }
    public List<String> protections(PackageType packageType) { return List.of("Moisture-absorbing beads", "Bubble-wrap bags"); }
}
