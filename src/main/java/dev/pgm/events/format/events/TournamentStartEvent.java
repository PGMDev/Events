package dev.pgm.events.format.events;

import dev.pgm.events.format.Tournament;
import org.bukkit.event.HandlerList;

public class TournamentStartEvent extends TournamentEvent {

  public TournamentStartEvent(Tournament tournament) {
    super(tournament);
  }

  private static final HandlerList handlers = new HandlerList();

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
