package dev.pgm.events.format.rounds.format;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.AbstractRound;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.RoundPhase;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import dev.pgm.events.format.rounds.veto.VetoRound;
import dev.pgm.events.format.score.FormattedScore;
import dev.pgm.events.team.TournamentTeam;
import java.util.HashMap;
import java.util.Map;
import tc.oc.pgm.api.match.Match;

public class FormatRound extends AbstractRound<FormatRoundSettings> {

  private FormatTournamentImpl formatTournament;
  private TournamentTeam winner;

  private TournamentTeam selectingTeam;

  public FormatRound(TournamentFormat format, FormatRoundSettings settings) {
    super(format, settings);
  }

  @Override
  public RoundDescription describe() {
    return new FormatRoundDescription(this);
  }

  @Override
  public void load() {
    setPhase(RoundPhase.WAITING);
    formatTournament =
        new FormatTournamentImpl(tournament().teamManager(), tournament().references(), this);
    for (RoundSettings settings : settings().roundSettings()) {
      TournamentRound round = settings.newRound(formatTournament);
      if (round instanceof VetoRound) ((VetoRound) round).setSelectingTeam(selectingTeam);
      formatTournament.addRound(round);
    }
  }

  @Override
  public void start(Match match) {
    setPhase(RoundPhase.RUNNING);
    formatTournament.nextRound(match);
  }

  @Override
  public void cleanup(Match match) {
    formatTournament.unregisterAll();
    formatTournament = null;

    setPhase(RoundPhase.FINISHED);
  }

  @Override
  public Map<TournamentTeam, Integer> scores() {
    Map<TournamentTeam, Integer> scores = new HashMap<TournamentTeam, Integer>();
    if (winner != null) scores.put(winner, 1);

    return scores;
  }

  public void setWinner(Match match, TournamentTeam winner) {
    this.winner = winner;
    tournament().nextRound(match);
  }

  public FormatTournamentImpl formatTournament() {
    return formatTournament;
  }

  public FormattedScore formattedScore() {
    return formatTournament.scores().formattedScore(formatTournament.teamManager());
  }

  public TournamentTeam getSelectingTeam() {
    return selectingTeam;
  }

  public void setSelectingTeam(TournamentTeam selectingTeam) {
    this.selectingTeam = selectingTeam;
  }
}
