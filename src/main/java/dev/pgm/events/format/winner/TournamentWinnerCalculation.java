package dev.pgm.events.format.winner;

import java.util.Map;
import java.util.Optional;

public interface TournamentWinnerCalculation<T> {

  Optional<T> winningTeam(int scoringRoundsPlayed, Map<T, Integer> scoreMap);
}
