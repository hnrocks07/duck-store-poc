package com.duckstore.store;

import static org.junit.jupiter.api.Assertions.*;

import com.duckstore.duck.DuckSize;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ChecklistRulesTest {
  private final ShippingStrategyFactory shipping =
      new ShippingStrategyFactory(
          List.of(
              new LandShippingStrategy(), new AirShippingStrategy(), new SeaShippingStrategy()));
  private final PricingCalculator calculator =
      new PricingCalculator(
          List.of(
              new VolumeDiscountRule(),
              new PackageAdjustmentRule(),
              new DestinationAdjustmentRule(),
              new ShippingChargeRule(shipping)));

  static Stream<Arguments> packagingCases() {
    return Stream.of(DuckSize.values())
        .flatMap(size -> Stream.of(ShippingMode.values()).map(mode -> Arguments.of(size, mode)));
  }

  @ParameterizedTest
  @MethodSource("packagingCases")
  void allFifteenPackagingCombinations(DuckSize size, ShippingMode mode) {
    var resolver =
        new PackagingResolver(
            new PackagingStrategyFactory(
                List.of(
                    new WoodPackagingStrategy(),
                    new CardboardPackagingStrategy(),
                    new PlasticPackagingStrategy())),
            shipping);
    var result = resolver.resolve(size, mode);
    var expected =
        switch (size) {
          case Large, XLarge -> PackageType.Wood;
          case Medium -> PackageType.Cardboard;
          default -> PackageType.Plastic;
        };
    assertEquals(expected, result.type());
    List<String> protections = List.of("Polystyrene balls");
    if (mode == ShippingMode.Sea)
      protections = List.of("Moisture-absorbing beads", "Bubble-wrap bags");
    if (mode == ShippingMode.Air && expected == PackageType.Plastic)
      protections = List.of("Bubble-wrap bags");
    assertEquals(protections, result.protections());
  }

  @Test
  void everyDisplayedLineSumsExactlyToTotal() {
    for (String price : List.of("0.10", "12.50", "0.01"))
      for (int quantity : List.of(1, 100, 101, 1000, 1001))
        for (String country : List.of("USA", "Bolivia", "India", "France"))
          for (var mode : ShippingMode.values())
            for (var type : PackageType.values()) {
              var result =
                  calculator.calculate(new BigDecimal(price), quantity, type, country, mode);
              assertEquals(
                  result.total(),
                  result.lines().stream()
                      .map(PricingLine::amount)
                      .reduce(new BigDecimal("0.00"), BigDecimal::add));
              result.lines().forEach(line -> assertEquals(2, line.amount().scale()));
              assertEquals(
                  mode == ShippingMode.Air && quantity > 1000,
                  result.lines().stream()
                      .anyMatch(line -> line.label().equals("Air bulk discount (15%)")));
            }
  }

  @Test
  void isolatedRulesMatchHandout() {
    for (int quantity : List.of(100, 101)) {
      var c = context(quantity, PackageType.Wood, "USA");
      new VolumeDiscountRule().apply(c);
      assertEquals(quantity == 100 ? 1 : 2, c.lines().size());
      if (quantity == 101) assertEquals(new BigDecimal("-20.20"), c.lines().getLast().amount());
    }
    var types = List.of(PackageType.Wood, PackageType.Plastic, PackageType.Cardboard);
    var amounts = List.of("5.00", "10.00", "-1.00");
    for (int i = 0; i < 3; i++) {
      var c = context(100, types.get(i), "USA");
      new PackageAdjustmentRule().apply(c);
      assertEquals(new BigDecimal(amounts.get(i)), c.lines().getLast().amount());
    }
    var countries = List.of(" USA ", "Bolivia", "india", "France", "US", "United States");
    var rates = List.of("18.00", "13.00", "19.00", "15.00", "15.00", "15.00");
    for (int i = 0; i < countries.size(); i++) {
      var c = context(100, PackageType.Wood, countries.get(i));
      new DestinationAdjustmentRule().apply(c);
      assertEquals(new BigDecimal(rates.get(i)), c.lines().getLast().amount());
    }
    assertEquals(new BigDecimal("70"), shipping.get(ShippingMode.Land).cost(7));
    assertEquals(new BigDecimal("400"), shipping.get(ShippingMode.Sea).cost(7));
    assertEquals(new BigDecimal("210"), shipping.get(ShippingMode.Air).cost(7));
  }

  private PricingContext context(int quantity, PackageType type, String country) {
    return new PricingContext(new BigDecimal("1.00"), quantity, type, country, ShippingMode.Land);
  }
}
