package dev.pgm.events.api.events;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.team.TournamentTeam;
import java.util.Optional;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class TournamentFinishedEvent extends TournamentEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  private final @Nullable TournamentTeam winningTeam;

  public TournamentFinishedEvent(
      TournamentFormat tournamentFormat, @Nullable TournamentTeam winningTeam) {
    super(tournamentFormat);
    this.winningTeam = winningTeam;
  }

  public Optional<TournamentTeam> winningTeam() {
    return Optional.ofNullable(winningTeam);
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}
