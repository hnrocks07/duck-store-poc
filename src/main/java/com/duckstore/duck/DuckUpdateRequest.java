package com.duckstore.duck;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
/** Deliberately excludes color and size: they cannot be changed through the edit API. */
public record DuckUpdateRequest(@NotNull @DecimalMin(value="0.01") @Digits(integer=10, fraction=2) BigDecimal price,
                                @Positive int quantity) { }
