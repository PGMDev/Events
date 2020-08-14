package dev.pgm.events.format.rounds.reference;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import java.util.UUID;

public class ReferenceRoundSettings extends RoundSettings {

  private final String futureID;
  private final String targetID;

  public ReferenceRoundSettings(String id, String targetID) {
    super(UUID.randomUUID().toString(), false, false);
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
    return new ReferenceRound(format, this);
  }

  @Override
  public TournamentRound newRound(TournamentFormat format, String id) {
    return new ReferenceRoundSettings(id, targetID).newRound(format);
  }
}
