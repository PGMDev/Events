package dev.pgm.events.format.rounds;

import net.md_5.bungee.api.chat.BaseComponent;

public interface RoundDescription {

  /**
   * A brief description of the current round. Relevant info can be, but is not limited to; Round
   * phase, scores, information the round uses from elsewhere(see ResultFromRound) Can include hover
   * info.
   *
   * @return a message describing the current round
   */
  BaseComponent roundInfo();

  /**
   * Small bit of info of current state of the round TODO REMOVE
   *
   * @return
   */
  String roundStatus();
}
