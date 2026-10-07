package com.duckstore.store;
import com.duckstore.duck.DuckSize;
public interface PackagingStrategy {
    boolean supports(DuckSize size);
    PackageType packageType();
}
