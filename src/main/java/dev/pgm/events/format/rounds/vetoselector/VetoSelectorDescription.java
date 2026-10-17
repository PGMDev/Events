package dev.pgm.events.format.rounds.vetoselector;

import dev.pgm.events.format.rounds.RoundDescription;
import net.kyori.adventure.text.Component;

public class VetoSelectorDescription implements RoundDescription {

  private final VetoSelectorRound vetoSelectorRound;

  public VetoSelectorDescription(VetoSelectorRound vetoSelectorRound) {
    this.vetoSelectorRound = vetoSelectorRound;
  }

  @Override
  public Component roundInfo() {
    return Component.text(vetoSelectorRound.getSelectingTeam().getName());
  }

  @Override
  public String roundStatus() {
    return "veto selector";
  }
}
