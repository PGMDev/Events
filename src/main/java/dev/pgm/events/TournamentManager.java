package dev.pgm.events;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.events.TournamentStartEvent;
import java.util.Optional;
import org.bukkit.event.EventPriority;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.util.bukkit.Events;

public class TournamentManager {

  private Tournament tournament = null;

  public Optional<Tournament> currentTournament() {
    return Optional.ofNullable(tournament);
  }

  public void createTournament(Match match, Tournament format) {
    if (this.tournament != null) this.tournament.unregisterAll();

    this.tournament = format;
    Events.callEvent(new TournamentStartEvent(format), EventPriority.NORMAL);
    format.nextRound(match);
  }

  public void deleteTournament() {
    tournament.suspend();
    tournament = null;
  }
}
