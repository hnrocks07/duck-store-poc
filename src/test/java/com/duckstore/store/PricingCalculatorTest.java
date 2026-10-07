package com.duckstore.store;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PricingCalculatorTest {
    private final PricingCalculator calculator = new PricingCalculator(java.util.List.of(new VolumeDiscountRule(), new PackageAdjustmentRule(), new DestinationAdjustmentRule(), new ShippingChargeRule()));
    @Test void applies_base_adjustments_and_air_discount_at_boundaries() {
        var result = calculator.calculate(new BigDecimal("10.00"), 1001, PackageType.Wood, "India", ShippingMode.Air);
        assertEquals(new BigDecimal("35935.90"), result.total());
    }
    @Test void uses_other_country_rate_and_sea_flat_fee() {
        var result = calculator.calculate(new BigDecimal("10.00"), 1, PackageType.Plastic, "France", ShippingMode.Sea);
        assertEquals(new BigDecimal("412.50"), result.total()); // 10 + 1 + 1.5 + 400
    }
    @Test void volume_discount_starts_only_above_100() {
        assertEquals(new BigDecimal("1570.00"), calculator.calculate(new BigDecimal("10"), 100, PackageType.Cardboard, "USA", ShippingMode.Sea).total());
        assertEquals(new BigDecimal("1379.70"), calculator.calculate(new BigDecimal("10"), 101, PackageType.Cardboard, "USA", ShippingMode.Sea).total());
    }
    @Test void air_discount_starts_only_above_1000() {
        assertEquals(new BigDecimal("30000.00"), calculator.calculate(BigDecimal.ZERO, 1000, PackageType.Wood, "USA", ShippingMode.Air).lines().getLast().amount());
        assertEquals(new BigDecimal("25525.50"), calculator.calculate(BigDecimal.ZERO, 1001, PackageType.Wood, "USA", ShippingMode.Air).lines().getLast().amount());
    }
    @Test void recognizes_bolivia_destination_rate() {
        assertEquals(new BigDecimal("411.20"), calculator.calculate(new BigDecimal("10"), 1, PackageType.Cardboard, "Bolivia", ShippingMode.Sea).total());
    }
}
