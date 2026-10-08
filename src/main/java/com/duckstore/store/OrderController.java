package com.duckstore.store;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @PostMapping("/price")
  public OrderResponse price(@Valid @RequestBody OrderRequest request) {
    return service.price(request);
  }
}
