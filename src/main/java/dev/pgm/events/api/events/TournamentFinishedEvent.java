package dev.pgm.events.api.events;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.team.TournamentTeam;
import java.util.Optional;

public class TournamentFinishedEvent extends TournamentEvent {

  private final Optional<TournamentTeam> winningTeam;

  public TournamentFinishedEvent(
      TournamentFormat tournamentFormat, Optional<TournamentTeam> winningTeam) {
    super(tournamentFormat);
    this.winningTeam = winningTeam;
  }

  public Optional<TournamentTeam> winningTeam() {
    return winningTeam;
  }
}
