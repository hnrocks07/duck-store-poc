package com.duckstore.store;

import com.duckstore.duck.*;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final DuckService ducks;
  private final PackagingResolver packaging;
  private final PricingCalculator pricing;

  public OrderService(DuckService ducks, PackagingResolver packaging, PricingCalculator pricing) {
    this.ducks = ducks;
    this.packaging = packaging;
    this.pricing = pricing;
  }

  public OrderResponse price(OrderRequest request) {
    Duck duck = ducks.cheapestActive(request.color(), request.size()); // documented selection rule
    var pack = packaging.resolve(request.size(), request.shippingMode());
    var calculated =
        pricing.calculate(
            duck.getPrice(),
            request.quantity(),
            pack.type(),
            request.destinationCountry(),
            request.shippingMode());
    return new OrderResponse(
        pack.type(), pack.protections(), calculated.total(), calculated.lines());
  }
}
