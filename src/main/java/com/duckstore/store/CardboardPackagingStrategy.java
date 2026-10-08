package com.duckstore.store;

import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;

@Component
public class CardboardPackagingStrategy implements PackagingStrategy {
  public boolean supports(DuckSize size) {
    return size == DuckSize.Medium;
  }

  public PackageType packageType() {
    return PackageType.Cardboard;
  }
}
