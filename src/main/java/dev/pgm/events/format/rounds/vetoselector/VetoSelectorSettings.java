package dev.pgm.events.format.rounds.vetoselector;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import java.util.UUID;

public class VetoSelectorSettings extends RoundSettings {

  public VetoSelectorSettings() {
    this(UUID.randomUUID().toString());
  }

  public VetoSelectorSettings(String id) {
    super(id, true, false);
  }

  @Override
  public TournamentRound newRound(Tournament format) {
    return new VetoSelectorRound(format, this);
  }

  @Override
  public TournamentRound newRound(Tournament format, String id) {
    return new VetoSelectorSettings(id).newRound(format);
  }
}
