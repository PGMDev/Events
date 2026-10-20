package dev.pgm.events.api.events;

import dev.pgm.events.format.TournamentFormat;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TournamentStartEvent extends TournamentEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public TournamentStartEvent(TournamentFormat tournamentFormat) {
    super(tournamentFormat);
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
