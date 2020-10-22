package dev.pgm.events.format.events;

import dev.pgm.events.format.Tournament;
import org.bukkit.event.Event;

public class TournamentEvent extends Event {

  private final Tournament tournament;

  public TournamentEvent(Tournament tournament) {
    this.tournament = tournament;
  }

  public Tournament format() {
    return tournament;
  }
}
