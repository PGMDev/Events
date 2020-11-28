package dev.pgm.events;

import dev.pgm.events.api.events.TournamentStartEvent;
import dev.pgm.events.format.TournamentFormat;
import java.util.Optional;
import org.bukkit.event.EventPriority;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.util.bukkit.Events;

public class TournamentManager {

  private TournamentFormat format = null;

  public Optional<TournamentFormat> currentTournament() {
    return Optional.ofNullable(format);
  }

  public void createTournament(Match match, TournamentFormat format) {
    if (this.format != null) this.format.unregisterAll();

    this.format = format;
    Events.callEvent(new TournamentStartEvent(format), EventPriority.NORMAL);
    format.nextRound(match);
  }

  public void deleteTournament() {
    format.suspend();
    format = null;
  }
}
