package dev.pgm.events.format.events;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.team.TournamentTeam;
import java.util.Optional;
import org.bukkit.event.HandlerList;

public class TournamentFinishedEvent extends TournamentEvent {

  private static final HandlerList handlers = new HandlerList();
  private final Optional<TournamentTeam> winningTeam;

  public TournamentFinishedEvent(
      TournamentFormat tournamentFormat, Optional<TournamentTeam> winningTeam) {
    super(tournamentFormat);
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
