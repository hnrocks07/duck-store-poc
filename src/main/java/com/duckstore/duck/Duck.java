package com.duckstore.duck;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ducks", uniqueConstraints = @UniqueConstraint(name = "duck_identity", columnNames = {"color", "size", "price", "deleted"}))
public class Duck {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Color color;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private DuckSize size;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(nullable = false) private int quantity;
    @Column(nullable = false) private boolean deleted = false;
    protected Duck() {}
    public Duck(Color color, DuckSize size, BigDecimal price, int quantity) { this.color=color; this.size=size; this.price=price; this.quantity=quantity; }
    public Long getId(){return id;} public Color getColor(){return color;} public DuckSize getSize(){return size;} public BigDecimal getPrice(){return price;} public int getQuantity(){return quantity;} public boolean isDeleted(){return deleted;}
    public void addQuantity(int quantity){this.quantity += quantity;}
    public void update(BigDecimal price, int quantity){this.price=price; this.quantity=quantity;}
    public void delete(){this.deleted=true;}
    public void restore(int quantity){this.deleted=false; this.quantity += quantity;}
}
