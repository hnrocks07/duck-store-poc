package com.duckstore.store;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ShippingStrategyFactory {
    private final Map<ShippingMode, ShippingStrategy> strategies = new EnumMap<>(ShippingMode.class);

    public ShippingStrategyFactory(List<ShippingStrategy> shippingStrategies) {
        shippingStrategies.forEach(strategy -> strategies.put(strategy.mode(), strategy));
    }

    public ShippingStrategy get(ShippingMode mode) { return strategies.get(mode); }
}
