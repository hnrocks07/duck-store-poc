package com.duckstore.store;

import java.math.BigDecimal;
import java.util.List;

public interface ShippingStrategy {
    ShippingMode mode();
    BigDecimal cost(int quantity);
    List<String> protections(PackageType packageType);
}
