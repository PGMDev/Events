package dev.pgm.events.format.rounds.resultfrom;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import java.util.UUID;

public class ResultFromSettings extends RoundSettings {

  private final String futureID;
  private final String targetID;

  public ResultFromSettings(String targetID) {
    this(UUID.randomUUID().toString(), targetID);
  }

  public ResultFromSettings(String id, String targetID) {
    super(id, true, false);
    this.futureID = id;
    this.targetID = targetID;
  }

  public String targetID() {
    return targetID;
  }

  public String futureID() {
    return futureID;
  }

  @Override
  public TournamentRound newRound(TournamentFormat format) {
    return new ResultFromRound(format, this);
  }

  @Override
  public TournamentRound newRound(TournamentFormat format, String id) {
    return new ResultFromSettings(id, targetID).newRound(format);
  }
}
