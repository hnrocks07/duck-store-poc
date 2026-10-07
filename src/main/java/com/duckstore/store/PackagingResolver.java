package com.duckstore.store;
import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class PackagingResolver {
    public Packaging resolve(DuckSize size, ShippingMode shippingMode) {
        PackageType type = switch (size) {
            case XLarge, Large -> PackageType.Wood;
            case Medium -> PackageType.Cardboard;
            case Small, XSmall -> PackageType.Plastic;
        };
        List<String> protections = switch (shippingMode) {
            case Sea -> List.of("Moisture-absorbing beads", "Bubble-wrap bags");
            case Land -> List.of("Polystyrene balls");
            case Air -> type == PackageType.Plastic ? List.of("Bubble-wrap bags") : List.of("Polystyrene balls");
        };
        return new Packaging(type, protections);
    }
    public record Packaging(PackageType type, List<String> protections) { }
}
