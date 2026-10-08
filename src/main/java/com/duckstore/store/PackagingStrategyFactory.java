package com.duckstore.store;

import com.duckstore.duck.DuckSize;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PackagingStrategyFactory {
  private final List<PackagingStrategy> strategies;

  public PackagingStrategyFactory(List<PackagingStrategy> strategies) {
    this.strategies = strategies;
  }

  public PackagingStrategy getStrategy(DuckSize size) {
    return strategies.stream().filter(s -> s.supports(size)).findFirst().orElseThrow();
  }
}
