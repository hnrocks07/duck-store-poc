package com.duckstore.store;
import java.math.*; import java.util.*;
public class PricingContext {
 private final BigDecimal base; private final int quantity; private final PackageType packageType; private final String country; private final ShippingMode mode; private final List<PricingLine> lines=new ArrayList<>();
 public PricingContext(BigDecimal unitPrice,int quantity,PackageType packageType,String country,ShippingMode mode){this.base=unitPrice.multiply(BigDecimal.valueOf(quantity));this.quantity=quantity;this.packageType=packageType;this.country=country;this.mode=mode;lines.add(new PricingLine("Merchandise",base));}
 public BigDecimal base(){return base;} public int quantity(){return quantity;} public PackageType packageType(){return packageType;} public String country(){return country;} public ShippingMode mode(){return mode;} public void add(String label,BigDecimal amount){lines.add(new PricingLine(label,amount.setScale(2,RoundingMode.HALF_UP)));} public List<PricingLine> lines(){return List.copyOf(lines);}
}
