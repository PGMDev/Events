package dev.pgm.events.format.rounds.veto;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import dev.pgm.events.format.rounds.format.FormatRound;
import dev.pgm.events.format.rounds.veto.settings.VetoOption;
import dev.pgm.events.format.rounds.veto.settings.VetoSettings;
import dev.pgm.events.team.TournamentTeam;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public class VetoController {

  private final TournamentFormat format;
  private final List<VetoOption> options;
  private final List<TournamentRound> toPlay;
  private final VetoSettings settings;

  private final List<TournamentTeam> rankedTeams;
  private final List<VetoHistory> history;
  private int vetoIndex = 0;

  public VetoController(
      TournamentFormat format, VetoSettings settings, List<TournamentTeam> ranked) {
    this.format = format;
    this.options = new ArrayList<>(settings.options());
    this.settings = settings;
    this.toPlay = new ArrayList<>();
    this.history = new ArrayList<>();
    this.rankedTeams = ranked;
  }

  public boolean hasMoreVetoing() {
    if (options.size() == 1) // only one map left
    return false;

    for (int i = vetoIndex; i < settings.vetoList().size(); i++)
      if (settings.vetoList().get(i).team > 0) return true; // teams still get to vote

    return true;
  }

  public VetoSettings.VetoType currentType() {
    return settings.vetoList().get(vetoIndex).vetoType;
  }

  public Duration currentVetoDuration() {
    if (settings.vetoList().get(vetoIndex).vetoDuration != null)
      return settings.vetoList().get(vetoIndex).vetoDuration;
    return settings.vetoTime();
  }

  public void completeVetos() {
    Random random = new Random();

    for (int i = vetoIndex; i < settings.vetoList().size(); i++) {
      VetoSettings.Veto veto = settings.vetoList().get(i);
      VetoOption option = options.remove(random.nextInt(options.size()));
      TournamentTeam team = veto.team == 0 ? null : rankedTeams.get(veto.team - 1);

      doVeto(option, veto.vetoType, team);
    }
  }

  public VetoHistory veto(@Nullable TournamentTeam team, int choice) {
    if (!hasMoreVetoing()) // maybe throw an exception?
    return null;

    VetoSettings.Veto veto = settings.vetoList().get(vetoIndex);
    VetoOption vetoOption = options.remove(choice);

    doVeto(vetoOption, veto.vetoType, team);

    VetoHistory history = new VetoHistory(team, veto.vetoType, vetoOption, veto.shouldAnnounce);
    if (history.shouldAnnounce()) this.history.add(history);

    vetoIndex++;
    return history;
  }

  private void doVeto(
      VetoOption vetoOption, VetoSettings.VetoType type, TournamentTeam selectingTeam) {
    switch (type) {
      case BAN:
        ban(vetoOption);
        break;
      case CHOOSE_FIRST:
        chooseFirst(vetoOption, selectingTeam);
        break;
      case CHOOSE_LAST:
        chooseLast(vetoOption, selectingTeam);
        break;
    }
  }

  public List<TournamentRound> toPlay() {
    return toPlay;
  }

  public List<VetoHistory> history() {
    return history;
  }

  public List<VetoOption> remainingOptions() {
    return options;
  }

  private void ban(VetoOption option) {}

  public boolean validVetoNumber(int number) {
    return number < options.size() && number >= 0;
  }

  private void chooseFirst(VetoOption option, TournamentTeam selectingTeam) {
    for (int i = option.rounds().size() - 1; i >= 0; i--) {
      TournamentRound round = option.rounds().get(i).newRound(format);
      if (round instanceof VetoRound) ((VetoRound) round).setSelectingTeam(selectingTeam);
      else if (round instanceof FormatRound) ((FormatRound) round).setSelectingTeam(selectingTeam);

      toPlay.add(0, round);
    }
  }

  private void chooseLast(VetoOption option, TournamentTeam selectingTeam) {
    for (RoundSettings settings : option.rounds()) {
      TournamentRound round = settings.newRound(format);
      if (round instanceof VetoRound) ((VetoRound) round).setSelectingTeam(selectingTeam);
      else if (round instanceof FormatRound) ((FormatRound) round).setSelectingTeam(selectingTeam);

      toPlay.add(round);
    }
  }

  /**
   * Returns the team that is picking for the current veto, in the case of an automatic veto NULL is
   * returned instead
   *
   * @return the current picking team or NULL if no team is picking
   */
  public Optional<TournamentTeam> picking() {
    int picker = settings.vetoList().get(vetoIndex).team;
    if (picker == 0) return Optional.empty();

    return Optional.of(rankedTeams.get(picker - 1));
  }

  /**
   * Returns true if the team specified is the same as the picking team, false othewise
   *
   * @param team the team to check if they are the same as the picking team
   * @return true if they're the same, false otherwise
   */
  public boolean isPicking(TournamentTeam team) {
    Optional<TournamentTeam> optPicking = picking();

    return optPicking.map(tournamentTeam -> tournamentTeam.equals(team)).orElse(team == null);
  }
}
