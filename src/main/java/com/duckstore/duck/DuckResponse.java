package com.duckstore.duck;
import java.math.BigDecimal;
public record DuckResponse(Long id, Color color, DuckSize size, BigDecimal price, int quantity) {
    static DuckResponse from(Duck d) { return new DuckResponse(d.getId(), d.getColor(), d.getSize(), d.getPrice(), d.getQuantity()); }
}
