package com.duckstore.store;
import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class CardboardPackagingStrategy implements PackagingStrategy {
 public boolean supports(DuckSize size){return size==DuckSize.Medium;}
 public PackagingResolver.Packaging create(ShippingMode mode){return new PackagingResolver.Packaging(PackageType.Cardboard, mode==ShippingMode.Sea?List.of("Moisture-absorbing beads","Bubble-wrap bags"):List.of("Polystyrene balls"));}
}
