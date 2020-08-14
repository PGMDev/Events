package dev.pgm.events.format.rounds.reference;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.AbstractRound;
import dev.pgm.events.format.rounds.RoundPhase;
import dev.pgm.events.format.rounds.TournamentRound;
import tc.oc.pgm.api.match.Match;

public class ReferenceRound extends AbstractRound<ReferenceRoundSettings> {

  private final TournamentFormat tournamentFormat;

  public ReferenceRound(TournamentFormat format, ReferenceRoundSettings settings) {
    super(format, settings);
    this.tournamentFormat = format;
  }

  @Override
  public ReferenceDescription describe() {
    return new ReferenceDescription(this);
  }

  @Override
  public void start(Match match) {
    setPhase(RoundPhase.RUNNING);
    TournamentRound round = tournamentFormat.references().needRound(settings().targetID());
    // add a copy after this round
    tournamentFormat.addRoundAfterCurrent(round.settings(), settings().futureID());
    // move to the next round (the one we just added)
    setPhase(RoundPhase.FINISHED);
    tournamentFormat.nextRound(match);
  }
}
