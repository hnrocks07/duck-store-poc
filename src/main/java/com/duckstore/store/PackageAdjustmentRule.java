package com.duckstore.store;
import org.springframework.core.annotation.Order; import org.springframework.stereotype.Component;
@Component @Order(2) public class PackageAdjustmentRule implements PricingRule { public void apply(PricingContext c){switch(c.packageType()){case Wood->c.add("Wood package surcharge (5%)",c.base().multiply(new java.math.BigDecimal("0.05")));case Plastic->c.add("Plastic package surcharge (10%)",c.base().multiply(new java.math.BigDecimal("0.10")));case Cardboard->c.add("Cardboard package discount (1%)",c.base().multiply(new java.math.BigDecimal("-0.01")));}} }
