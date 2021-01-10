package dev.pgm.events.format.score;

import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.utils.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class Scores {

  private final Map<TournamentTeam, Score> scores;
  private final Collection<? extends TournamentTeam> updated;

  private Scores(Map<TournamentTeam, Score> scores, Collection<? extends TournamentTeam> updated) {
    // convert to static, throw exception if length of scores map == 0
    this.scores = scores;
    this.updated = updated;
  }

  public static Scores ofLegacy(
      Map<TournamentTeam, Integer> scores, Collection<? extends TournamentTeam> updated) {
    return of(
        scores.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, x -> new Score(x.getKey(), x.getValue()))),
        updated);
  }

  public static Scores of(
      Map<TournamentTeam, Score> scores, Collection<? extends TournamentTeam> updated) {
    return new Scores(scores, updated);
  }

  public Score score(TournamentTeam tournamentTeam) {
    return scores.getOrDefault(tournamentTeam, new Score(tournamentTeam, 0));
  }

  public Collection<? extends Score> scores() {
    return scores.values();
  }

  public FormattedScore formattedScore(TournamentTeamManager teamManager) {
    if (scores.size() == 2) {
      // size == 2, therefore display two way score
      Pair<Score, Score> topTwo = topTwoOrdered();
      return new TwoTeamFormattedScore(teamManager, topTwo, updated);
    }

    // TODO: when size != 2, return just the top score
    return null;
  }

  public Pair<Score, Score> topTwoOrdered() {
    TournamentTeam highest = null;
    int highestScore = -1;

    TournamentTeam second = null;
    int secondHighestScore = -1;

    for (Score score : scores.values()) {
      if (score.score() > highestScore) {
        secondHighestScore = highestScore;
        second = highest;
        highestScore = score.score();
        highest = score.team();
      } else if (score.score() > secondHighestScore) {
        second = score.team();
        secondHighestScore = score.score();
      }
    }

    return Pair.create(new Score(highest, highestScore), new Score(second, secondHighestScore));
  }

  public Score highestScore() {
    TournamentTeam highest = null;
    int highestScore = -1;
    for (Score score : scores.values()) {
      if (score.score() > highestScore) {
        highestScore = score.score();
        highest = score.team();
      }
    }
    return new Score(highest, highestScore);
  }
}
