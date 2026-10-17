package dev.pgm.events.format.rounds;

import net.kyori.adventure.text.Component;

public interface RoundDescription {

  /**
   * The main round info, also contains hover information for scores in match, time and stuff like
   * that
   */
  Component roundInfo();

  /** Small bit of info of current state of the round */
  String roundStatus();
}
