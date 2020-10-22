package dev.pgm.events.format.events;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.team.TournamentTeam;
import java.util.Optional;
import org.bukkit.event.HandlerList;

public class TournamentFinishedEvent extends TournamentEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Optional<TournamentTeam> winningTeam;

  public TournamentFinishedEvent(Tournament tournament, Optional<TournamentTeam> winningTeam) {
    super(tournament);
    this.winningTeam = winningTeam;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  public Optional<TournamentTeam> winningTeam() {
    return winningTeam;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}
