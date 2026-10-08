package com.duckstore.store;

import java.util.List;

/** Shared immutable protection descriptions used by shipping strategies. */
final class Protection {
  static final List<String> POLYSTYRENE = List.of("Polystyrene balls");
  static final List<String> BUBBLE_WRAP = List.of("Bubble-wrap bags");
  static final List<String> SEA = List.of("Moisture-absorbing beads", "Bubble-wrap bags");

  private Protection() {}
}
