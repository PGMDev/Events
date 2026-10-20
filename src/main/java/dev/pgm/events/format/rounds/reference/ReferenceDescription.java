package dev.pgm.events.format.rounds.reference;

import static net.kyori.adventure.text.Component.text;

import dev.pgm.events.format.rounds.RoundDescription;
import net.kyori.adventure.text.Component;

public class ReferenceDescription implements RoundDescription {

  private final ReferenceRound referenceRound;

  public ReferenceDescription(ReferenceRound referenceRound) {
    this.referenceRound = referenceRound;
  }

  @Override
  public Component roundInfo() {
    return text("Reference round -> referencing: " + referenceRound.settings().targetID());
  }

  @Override
  public String roundStatus() {
    return "reference round";
  }
}
