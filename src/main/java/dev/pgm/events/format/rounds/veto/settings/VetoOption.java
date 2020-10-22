package dev.pgm.events.format.rounds.veto.settings;

import dev.pgm.events.format.rounds.RoundSettings;
import java.util.List;

/** Something that can be vetoed */
public class VetoOption {

  private final List<RoundSettings> rounds;
  private final String name;

  /**
   * Something that can be vetoed.
   *
   * @param rounds is the rounds that will be played if this option is the winner
   * @param name is the name of this option (e.g "DTM" or "Airship Battles")
   */
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
