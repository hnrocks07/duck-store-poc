package com.duckstore.store;

import static org.junit.jupiter.api.Assertions.*;
import com.duckstore.duck.DuckSize;
import org.junit.jupiter.api.Test;

class PackagingResolverTest {
    private final PackagingResolver resolver = new PackagingResolver();
    @Test void chooses_all_required_package_and_protection_combinations() {
        var airWood = resolver.resolve(DuckSize.Large, ShippingMode.Air);
        assertEquals(PackageType.Wood, airWood.type()); assertEquals(java.util.List.of("Polystyrene balls"), airWood.protections());
        var airPlastic = resolver.resolve(DuckSize.Small, ShippingMode.Air);
        assertEquals(PackageType.Plastic, airPlastic.type()); assertEquals(java.util.List.of("Bubble-wrap bags"), airPlastic.protections());
        var land = resolver.resolve(DuckSize.Medium, ShippingMode.Land);
        assertEquals(PackageType.Cardboard, land.type()); assertEquals(java.util.List.of("Polystyrene balls"), land.protections());
        var sea = resolver.resolve(DuckSize.XSmall, ShippingMode.Sea);
        assertEquals(java.util.List.of("Moisture-absorbing beads", "Bubble-wrap bags"), sea.protections());
    }
}
