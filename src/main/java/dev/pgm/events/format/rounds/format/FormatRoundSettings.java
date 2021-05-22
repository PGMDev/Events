package dev.pgm.events.format.rounds.format;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import java.util.ArrayList;
import java.util.List;

public class FormatRoundSettings extends RoundSettings {

  private final String name;
  private final List<RoundSettings> rounds;
  private final int bestOf;

  public FormatRoundSettings(String id, String name, List<RoundSettings> rounds, int bestOf) {
    super(id, true, false);
    this.name = name;
    this.rounds = rounds;
    this.bestOf = bestOf;
  }

  public String name() {
    return name;
  }

  public List<RoundSettings> roundSettings() {
    return rounds;
  }

  public int bestOf() {
    return bestOf;
  }

  @Override
  public TournamentRound newRound(TournamentFormat format) {
    return new FormatRound(format, this);
  }

  @Override
  public TournamentRound newRound(TournamentFormat format, String id) {
    return new FormatRoundSettings(id, name, new ArrayList<RoundSettings>(rounds), bestOf)
        .newRound(format);
  }
}
