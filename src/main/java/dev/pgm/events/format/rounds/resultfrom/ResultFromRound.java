package dev.pgm.events.format.rounds.resultfrom;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.rounds.AbstractRound;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.RoundPhase;
import dev.pgm.events.team.TournamentTeam;
import java.util.Map;
import tc.oc.pgm.api.match.Match;

public class ResultFromRound extends AbstractRound<ResultFromSettings> {

  private Tournament format;

  public ResultFromRound(Tournament format, ResultFromSettings settings) {
    super(format, settings);
    this.format = format;
  }

  @Override
  public RoundDescription describe() {
    return new ResultFromDescription(this);
  }

  @Override
  public void start(Match match) {
    setPhase(RoundPhase.FINISHED);
    tournament().nextRound(match);
  }

  @Override
  public Map<TournamentTeam, Integer> scores() {
    return format.references().needRound(settings().targetID()).scores();
  }
}
