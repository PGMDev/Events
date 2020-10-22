package dev.pgm.events.format.rounds.replay;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import dev.pgm.events.format.rounds.reference.ReferenceRoundSettings;
import java.util.List;
import java.util.UUID;

public class ReplaySettings extends RoundSettings {

  private final String futureID;
  private final List<ReferenceRoundSettings> referenceSettings;

  public ReplaySettings(String id, List<ReferenceRoundSettings> referenceSettings) {
    super(UUID.randomUUID().toString(), false, false);
    this.futureID = id;
    this.referenceSettings = referenceSettings;
  }

  public String futureID() {
    return futureID;
  }

  public List<ReferenceRoundSettings> referenceSettings() {
    return referenceSettings;
  }

  @Override
  public TournamentRound newRound(Tournament format) {
    return new ReplayRound(format, this);
  }

  @Override
  public TournamentRound newRound(Tournament format, String id) {
    return new ReplaySettings(id, referenceSettings).newRound(format);
  }
}
