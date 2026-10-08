package com.duckstore.duck;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record DuckRequest(
    @NotNull Color color,
    @NotNull DuckSize size,
    @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal price,
    @Positive int quantity) {}
