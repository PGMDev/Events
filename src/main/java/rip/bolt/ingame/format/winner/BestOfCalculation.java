package rip.bolt.ingame.format.winner;

import java.util.Map;
import java.util.Optional;

public class BestOfCalculation<T> implements TournamentWinnerCalculation<T> {

    private final int number;

    public BestOfCalculation(int number) {
        this.number = number;
    }

    @Override
    public Optional<T> winningTeam(int scoringRoundsPlayed, Map<T, Integer> scoreMap) {
        //if: scoringRoundsPlayed >= this.number, highest score team wins
        //else: diff between highest two = this.number - scoringRoundsPlayer
        if (scoringRoundsPlayed >= this.number)
            return differenceNeeded(1, scoreMap);

        return differenceNeeded(this.number - scoringRoundsPlayed + 1, scoreMap);
    }

    private Optional<T> differenceNeeded(int diffNeeded, Map<T, Integer> scoreMap) {
        int highestScore = 0;
        int lowestDifference = 0;
        T highest = null;

        for (Map.Entry<T, Integer> entry : scoreMap.entrySet()) {
            int score = entry.getValue();
            if (score > highestScore) {
                lowestDifference = score - highestScore;
                highestScore = score;
                highest = entry.getKey();
            } else {
                int difference = highestScore - score;
                if (difference < lowestDifference) {
                    lowestDifference = difference;
                }
            }
        }

        if (lowestDifference >= diffNeeded && highest != null)
            return Optional.of(highest);

        return Optional.empty();
    }
}
