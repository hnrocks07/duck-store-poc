package com.duckstore.duck;

import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DuckService {
  private final DuckRepository ducks;
  private final DuckWriter writer;

  public DuckService(DuckRepository ducks, DuckWriter writer) {
    this.ducks = ducks;
    this.writer = writer;
  }

  public List<DuckResponse> list() {
    return ducks.findByDeletedFalseOrderByQuantityDescIdAsc().stream()
        .map(DuckResponse::from)
        .toList();
  }

  public DuckResponse add(DuckRequest request) {
    try {
      return writer.addOrMerge(request);
    } catch (DataIntegrityViolationException race) {
      return writer.addOrMerge(request);
    }
  }

  @Transactional
  public DuckResponse update(long id, DuckUpdateRequest request) {
    Duck duck = active(id);
    if (ducks.existsByColorAndSizeAndPriceAndIdNot(
        duck.getColor(), duck.getSize(), request.price(), duck.getId())) {
      throw new DuplicateDuckException();
    }
    duck.update(request.price(), request.quantity());
    return DuckResponse.from(duck);
  }

  @Transactional
  public void delete(long id) {
    active(id).delete();
  }

  public Duck cheapestActive(Color color, DuckSize size) {
    return ducks
        .findFirstByColorAndSizeAndDeletedFalseOrderByPriceAscIdAsc(color, size)
        .orElseThrow(
            () -> new NotFoundException("No active duck exists for " + color + " / " + size + "."));
  }

  private Duck active(long id) {
    return ducks
        .findById(id)
        .filter(d -> !d.isDeleted())
        .orElseThrow(() -> new NotFoundException("Active duck " + id + " was not found."));
  }
}
