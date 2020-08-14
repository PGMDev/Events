package dev.pgm.events.format.events;

import dev.pgm.events.format.TournamentFormat;
import org.bukkit.event.Event;

public class TournamentEvent extends Event {

  private final TournamentFormat tournamentFormat;

  public TournamentEvent(TournamentFormat tournamentFormat) {
    this.tournamentFormat = tournamentFormat;
  }

  public TournamentFormat format() {
    return tournamentFormat;
  }
}
