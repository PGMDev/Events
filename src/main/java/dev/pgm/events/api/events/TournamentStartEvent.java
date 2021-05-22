package dev.pgm.events.api.events;

import dev.pgm.events.format.TournamentFormat;

public class TournamentStartEvent extends TournamentEvent {

  public TournamentStartEvent(TournamentFormat tournamentFormat) {
    super(tournamentFormat);
  }
}
