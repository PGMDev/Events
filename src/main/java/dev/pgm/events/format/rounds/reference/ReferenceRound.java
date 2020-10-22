package dev.pgm.events.format.rounds.reference;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.rounds.AbstractRound;
import dev.pgm.events.format.rounds.RoundPhase;
import dev.pgm.events.format.rounds.TournamentRound;
import tc.oc.pgm.api.match.Match;

public class ReferenceRound extends AbstractRound<ReferenceRoundSettings> {

  private final Tournament tournament;

  public ReferenceRound(Tournament format, ReferenceRoundSettings settings) {
    super(format, settings);
    this.tournament = format;
  }

  @Override
  public ReferenceDescription describe() {
    return new ReferenceDescription(this);
  }

  @Override
  public void start(Match match) {
    setPhase(RoundPhase.RUNNING);
    TournamentRound round = tournament.references().needRound(settings().targetID());
    // add a copy after this round
    tournament.addRoundAfterCurrent(round.settings(), settings().futureID());
    // move to the next round (the one we just added)
    setPhase(RoundPhase.FINISHED);
    tournament.nextRound(match);
  }
}
