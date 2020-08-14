package dev.pgm.events.format.rounds.vetoselector;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.AbstractRound;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.RoundPhase;
import dev.pgm.events.team.TournamentTeam;
import java.util.HashMap;
import java.util.Map;
import tc.oc.pgm.api.match.Match;

public class VetoSelectorRound extends AbstractRound<VetoSelectorSettings> {

  private TournamentTeam selectingTeam;

  public VetoSelectorRound(TournamentFormat format, VetoSelectorSettings settings) {
    super(format, settings);
  }

  @Override
  public RoundDescription describe() {
    return new VetoSelectorDescription(this);
  }

  @Override
  public void start(Match match) {
    setPhase(RoundPhase.FINISHED);
    tournament().nextRound(match);
  }

  @Override
  public Map<TournamentTeam, Integer> scores() {
    Map<TournamentTeam, Integer> scores = new HashMap<TournamentTeam, Integer>();
    if (selectingTeam != null) scores.put(selectingTeam, 1);

    return scores;
  }

  public TournamentTeam getSelectingTeam() {
    return selectingTeam;
  }

  public void setSelectingTeam(TournamentTeam selectingTeam) {
    this.selectingTeam = selectingTeam;
  }
}
