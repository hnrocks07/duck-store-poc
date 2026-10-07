package com.duckstore.store;
import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class WoodPackagingStrategy implements PackagingStrategy {
 public boolean supports(DuckSize size){return size==DuckSize.XLarge||size==DuckSize.Large;}
 public PackagingResolver.Packaging create(ShippingMode mode){return new PackagingResolver.Packaging(PackageType.Wood, mode==ShippingMode.Sea?List.of("Moisture-absorbing beads","Bubble-wrap bags"):List.of("Polystyrene balls"));}
}
