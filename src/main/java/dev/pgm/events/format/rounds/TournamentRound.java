package dev.pgm.events.format.rounds;

import dev.pgm.events.team.TournamentTeam;
import java.util.Map;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.Match;

public interface TournamentRound extends Listener {

  String id();

  RoundDescription describe();

  void load();

  void start(Match match);

  void cleanup(Match match);

  RoundPhase phase();

  Map<TournamentTeam, Integer> scores();

  /** @return true if the scores of this count to the overall series, false otherwise */
  default boolean isScoring() {
    return settings().scoring();
  }

  boolean shouldShowInHistory();

  TournamentRound currentRound();

  RoundSettings settings();
}
