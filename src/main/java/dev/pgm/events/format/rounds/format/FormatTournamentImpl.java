package dev.pgm.events.format.rounds.format;

import dev.pgm.events.format.RoundReferenceHolder;
import dev.pgm.events.format.TournamentFormatImpl;
import dev.pgm.events.format.TournamentRoundOptions;
import dev.pgm.events.format.winner.BestOfCalculation;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import java.time.Duration;
import java.util.Optional;
import tc.oc.pgm.api.match.Match;

public class FormatTournamentImpl extends TournamentFormatImpl {

  private FormatRound formatRound;

  public FormatTournamentImpl(
      TournamentTeamManager teamManager, RoundReferenceHolder references, FormatRound formatRound) {
    super(
        teamManager,
        new TournamentRoundOptions(
            false,
            false,
            false,
            Duration.ofSeconds(20),
            Duration.ofSeconds(30),
            Duration.ofSeconds(40),
            new BestOfCalculation<>(formatRound.settings().bestOf())),
        references);
    this.formatRound = formatRound;
  }

  @Override
  public void onEnd(Match match, Optional<TournamentTeam> winner) {
    formatRound.setWinner(match, winner.isPresent() ? winner.get() : null);
  }

  public FormatRound getFormatRound() {
    return formatRound;
  }
}
