package dev.pgm.events.format.rounds.resultfrom;

import dev.pgm.events.format.rounds.RoundDescription;
import net.kyori.adventure.text.Component;

public class ResultFromDescription implements RoundDescription {

  private final ResultFromRound resultFromRound;

  public ResultFromDescription(ResultFromRound resultFromRound) {
    this.resultFromRound = resultFromRound;
  }

  @Override
  public Component roundInfo() {
    return Component.text(
        "ResultFrom round -> using result from: " + resultFromRound.settings().targetID());
  }

  @Override
  public String roundStatus() {
    return "result from round";
  }
}
