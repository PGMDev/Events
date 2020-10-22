package dev.pgm.events.format.winner;

import java.util.Map;
import java.util.Optional;

/** @param <T> something that can win a tournament */
public interface TournamentWinnerCalculation<T> {

  Optional<T> winningTeam(int scoringRoundsPlayed, Map<T, Integer> scoreMap);
}
