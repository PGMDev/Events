package dev.pgm.events.api.events;

import dev.pgm.events.format.TournamentFormat;
import org.bukkit.event.Event;

public abstract class TournamentEvent extends Event {

  private final TournamentFormat tournamentFormat;

  public TournamentEvent(TournamentFormat tournamentFormat) {
    this.tournamentFormat = tournamentFormat;
  }

  public TournamentFormat format() {
    return tournamentFormat;
  }
}
