package dev.pgm.events.format.rounds.resultfrom;

import dev.pgm.events.format.rounds.RoundDescription;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ResultFromDescription implements RoundDescription {

  private final ResultFromRound resultFromRound;

  public ResultFromDescription(ResultFromRound resultFromRound) {
    this.resultFromRound = resultFromRound;
  }

  @Override
  public BaseComponent roundInfo() {
    return new TextComponent(
        "ResultFrom round -> using result from: " + resultFromRound.settings().targetID());
  }

  @Override
  public String roundStatus() {
    return "result from round";
  }
}
