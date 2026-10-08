package com.duckstore.store;

import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;

@Component
public class PackagingResolver {
  private final PackagingStrategyFactory factory;
  private final ShippingStrategyFactory shipping;

  public PackagingResolver(PackagingStrategyFactory factory, ShippingStrategyFactory shipping) {
    this.factory = factory;
    this.shipping = shipping;
  }

  public Packaging resolve(DuckSize size, ShippingMode shippingMode) {
    PackageType packageType = factory.getStrategy(size).packageType();
    return new Packaging(packageType, shipping.get(shippingMode).protections(packageType));
  }

  public record Packaging(PackageType type, java.util.List<String> protections) {}
}
