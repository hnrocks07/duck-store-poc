package com.duckstore.store;
import org.springframework.core.annotation.Order; import org.springframework.stereotype.Component; import java.math.*;
@Component @Order(4) public class ShippingChargeRule implements PricingRule {
 private final ShippingStrategyFactory shipping;
 public ShippingChargeRule(ShippingStrategyFactory shipping){this.shipping=shipping;}
 public void apply(PricingContext c){BigDecimal a=shipping.get(c.mode()).cost(c.quantity());c.add(c.mode()+" shipping",a);if(c.mode()==ShippingMode.Air&&c.quantity()>1000)c.add("Air bulk discount (15%)",a.multiply(new BigDecimal("-.15")));}
}
