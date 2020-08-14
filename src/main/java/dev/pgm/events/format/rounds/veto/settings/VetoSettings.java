package dev.pgm.events.format.rounds.veto.settings;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import dev.pgm.events.format.rounds.veto.VetoRound;
import java.time.Duration;
import java.util.List;

public class VetoSettings extends RoundSettings {

  private final Duration vetoTime;

  private final RoundSettings decider;

  // private final int mapsProduced;
  private final List<Veto> vetoList;
  private final int score;
  private final List<VetoOption> options;

  public VetoSettings(
      String id,
      Duration vetoTime,
      RoundSettings decider,
      int score,
      boolean scoring,
      boolean showInHistory,
      List<Veto> vetoList,
      List<VetoOption> options) {
    super(id, scoring, showInHistory);
    this.score = score;
    this.vetoTime = vetoTime;
    this.decider = decider;
    this.vetoList = vetoList;
    this.options = options;
  }

  @Override
  public TournamentRound newRound(TournamentFormat format) {
    return new VetoRound(format, this);
  }

  @Override
  public TournamentRound newRound(TournamentFormat format, String id) {
    return new VetoSettings(
            id, vetoTime, decider, score, scoring(), showInHistory(), vetoList, options)
        .newRound(format);
  }

  public int score() {
    return score;
  }

  public Duration vetoTime() {
    return vetoTime;
  }

  public RoundSettings decider() {
    return decider;
  }

  public List<Veto> vetoList() {
    return vetoList;
  }

  public List<VetoOption> options() {
    return options;
  }

  public enum VetoType {
    BAN,
    CHOOSE_FIRST,
    CHOOSE_LAST
  }

  public static class Veto {
    public final VetoType vetoType;
    public final int team;
    public Duration vetoDuration;
    public final boolean shouldAnnounce;

    public Veto(VetoType vetoType, int team, Duration vetoDuration, boolean shouldAnnounce) {
      this.vetoType = vetoType;
      this.team = team;
      this.vetoDuration = vetoDuration;
      this.shouldAnnounce = shouldAnnounce;
    }
  }
}
