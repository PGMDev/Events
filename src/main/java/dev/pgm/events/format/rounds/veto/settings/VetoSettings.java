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
    validate(vetoList, options);
    this.score = score;
    this.vetoTime = vetoTime;
    this.decider = decider;
    this.vetoList = vetoList;
    this.options = options;
  }

  private VetoSettings(VetoSettings source, String id) {
    super(id, source.scoring(), source.showInHistory());
    this.score = source.score;
    this.vetoTime = source.vetoTime;
    this.decider = source.decider;
    this.vetoList = source.vetoList;
    this.options = source.options;
  }

  // the veto controller relies on these: each step consumes exactly one option, and the system
  // enacts the last step once no choice is left
  private static void validate(List<Veto> vetoList, List<VetoOption> options) {
    if (options.size() < 2)
      throw new IllegalArgumentException(
          "Veto needs at least 2 options, but has " + options.size() + ".");
    if (vetoList.size() != options.size())
      throw new IllegalArgumentException("Veto order has "
          + vetoList.size()
          + " steps but there are "
          + options.size()
          + " options. The order must contain exactly one step per option.");
    for (Veto veto : vetoList) {
      if (veto.team() < 0)
        throw new IllegalArgumentException(
            "Invalid team " + veto.team() + ". It must not be negative.");
    }
    if (vetoList.stream().allMatch(veto -> veto.vetoType() == VetoType.BAN))
      throw new IllegalArgumentException(
          "Veto order has no pick steps, so no rounds would be played.");
    if (vetoList.getLast().team() != 0)
      throw new IllegalArgumentException(
          "The last step of a veto order is enacted by the system and must not have a team.");
  }

  @Override
  public TournamentRound newRound(TournamentFormat format) {
    return new VetoRound(format, this);
  }

  @Override
  public TournamentRound newRound(TournamentFormat format, String id) {
    return new VetoSettings(this, id).newRound(format);
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

  public record Veto(VetoType vetoType, int team, Duration vetoDuration, boolean shouldAnnounce) {}
}
