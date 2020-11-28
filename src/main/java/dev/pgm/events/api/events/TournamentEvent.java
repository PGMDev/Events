package dev.pgm.events.api.events;

import dev.pgm.events.format.TournamentFormat;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TournamentEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private final TournamentFormat tournamentFormat;

  public TournamentEvent(TournamentFormat tournamentFormat) {
    this.tournamentFormat = tournamentFormat;
  }

  public TournamentFormat format() {
    return tournamentFormat;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
