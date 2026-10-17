package dev.pgm.events.format.rounds.replay;

import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.RoundPhase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ReplayDescription implements RoundDescription {

  private final ReplayRound round;

  public ReplayDescription(ReplayRound round) {
    this.round = round;
  }

  @Override
  public Component roundInfo() {
    // haven't yet decided if we're gonna play if here
    if (round.phase() != RoundPhase.FINISHED)
      return Component.text("Replay tied round", NamedTextColor.GRAY);

    // not gonna replay
    if (round.shouldShowInHistory())
      return Component.text("Not replaying a round", NamedTextColor.GRAY)
          .decorate(TextDecoration.STRIKETHROUGH);

    return Component.text("Error replaying a round");
  }

  @Override
  public String roundStatus() {
    return null;
  }
}
