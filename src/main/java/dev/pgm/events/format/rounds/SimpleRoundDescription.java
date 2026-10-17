package dev.pgm.events.format.rounds;

import net.kyori.adventure.text.Component;

public class SimpleRoundDescription implements RoundDescription {

  private final String status;
  private final Component roundInfo;

  public SimpleRoundDescription(String status, Component roundInfo) {
    this.status = status;
    this.roundInfo = roundInfo;
  }

  @Override
  public Component roundInfo() {
    return roundInfo;
  }

  @Override
  public String roundStatus() {
    return status;
  }
}
