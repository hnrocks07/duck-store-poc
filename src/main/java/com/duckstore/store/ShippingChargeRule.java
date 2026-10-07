package com.duckstore.store;
import org.springframework.core.annotation.Order; import org.springframework.stereotype.Component; import java.math.*;
@Component @Order(4) public class ShippingChargeRule implements PricingRule { public void apply(PricingContext c){BigDecimal a=switch(c.mode()){case Sea->new BigDecimal("400");case Land->new BigDecimal("10").multiply(BigDecimal.valueOf(c.quantity()));case Air->new BigDecimal("30").multiply(BigDecimal.valueOf(c.quantity()));};if(c.mode()==ShippingMode.Air&&c.quantity()>1000)a=a.multiply(new BigDecimal(".85"));c.add(c.mode()+" shipping",a);} }
