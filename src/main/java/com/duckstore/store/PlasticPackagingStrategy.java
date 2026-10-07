package com.duckstore.store;
import com.duckstore.duck.DuckSize;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class PlasticPackagingStrategy implements PackagingStrategy {
 public boolean supports(DuckSize size){return size==DuckSize.Small||size==DuckSize.XSmall;}
 public PackageType packageType(){return PackageType.Plastic;}
}
