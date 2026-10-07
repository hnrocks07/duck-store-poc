package com.duckstore.store;
import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class PlasticPackagingStrategy implements PackagingStrategy {
 public boolean supports(DuckSize size){return size==DuckSize.Small||size==DuckSize.XSmall;}
 public PackagingResolver.Packaging create(ShippingMode mode){return new PackagingResolver.Packaging(PackageType.Plastic, mode==ShippingMode.Air?List.of("Bubble-wrap bags"):mode==ShippingMode.Sea?List.of("Moisture-absorbing beads","Bubble-wrap bags"):List.of("Polystyrene balls"));}
}
