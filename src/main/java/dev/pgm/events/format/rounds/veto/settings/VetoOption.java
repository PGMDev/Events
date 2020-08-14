package dev.pgm.events.format.rounds.veto.settings;

import dev.pgm.events.format.rounds.RoundSettings;
import java.util.List;

public class VetoOption {

  private final List<RoundSettings> rounds;
  private final String name;

  public VetoOption(List<RoundSettings> rounds, String name) {
    this.rounds = rounds;
    this.name = name;
  }

  public List<RoundSettings> rounds() {
    return rounds;
  }

  public String name() {
    return name;
  }
}
