package com.duckstore.duck;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DuckWriter {
  private final DuckRepository ducks;

  public DuckWriter(DuckRepository ducks) {
    this.ducks = ducks;
  }

  @Transactional
  public DuckResponse addOrMerge(DuckRequest request) {
    var active = ducks.lockByIdentity(request.color(), request.size(), request.price(), false);
    if (active.isPresent()) {
      active.get().addQuantity(request.quantity());
      return DuckResponse.from(active.get());
    }

    var deleted = ducks.lockByIdentity(request.color(), request.size(), request.price(), true);
    if (deleted.isPresent()) {
      deleted.get().restore(request.quantity());
      return DuckResponse.from(deleted.get());
    }

    Duck duck =
        ducks.saveAndFlush(
            new Duck(request.color(), request.size(), request.price(), request.quantity()));
    return DuckResponse.from(duck);
  }
}
