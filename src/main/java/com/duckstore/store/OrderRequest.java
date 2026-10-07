package com.duckstore.store;
import com.duckstore.duck.*;
import jakarta.validation.constraints.*;
public record OrderRequest(@NotNull Color color, @NotNull DuckSize size, @Positive int quantity,
                           @NotBlank String destinationCountry, @NotNull ShippingMode shippingMode) { }
