package dev.pgm.events.format.rounds.reference;

import dev.pgm.events.format.rounds.RoundDescription;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ReferenceDescription implements RoundDescription {

  private final ReferenceRound referenceRound;

  public ReferenceDescription(ReferenceRound referenceRound) {
    this.referenceRound = referenceRound;
  }

  @Override
  public BaseComponent roundInfo() {
    return new TextComponent(
        "Reference round -> referencing: " + referenceRound.settings().targetID());
  }

  @Override
  public String roundStatus() {
    return "reference round";
  }
}
