package com.duckstore.store;

import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;

@Component
public class WoodPackagingStrategy implements PackagingStrategy {
  public boolean supports(DuckSize size) {
    return size == DuckSize.XLarge || size == DuckSize.Large;
  }

  public PackageType packageType() {
    return PackageType.Wood;
  }
}
