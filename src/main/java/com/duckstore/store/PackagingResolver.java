package com.duckstore.store;
import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;

@Component
public class PackagingResolver {
    private final PackagingStrategyFactory factory;
    public PackagingResolver(PackagingStrategyFactory factory){this.factory=factory;}
    public Packaging resolve(DuckSize size, ShippingMode shippingMode) {
        return factory.getStrategy(size).create(shippingMode);
    }
    public record Packaging(PackageType type, java.util.List<String> protections) { }
}
