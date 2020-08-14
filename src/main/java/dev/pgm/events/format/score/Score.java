package dev.pgm.events.format.score;

import dev.pgm.events.team.TournamentTeam;

public class Score {

  private final TournamentTeam tournamentTeam;
  private final int score;

  public Score(TournamentTeam tournamentTeam, int score) {
    this.tournamentTeam = tournamentTeam;
    this.score = score;
  }

  public TournamentTeam team() {
    return tournamentTeam;
  }

  public int score() {
    return score;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Score score1 = (Score) o;

    if (score != score1.score) return false;
    return tournamentTeam.equals(score1.tournamentTeam);
  }

  @Override
  public int hashCode() {
    int result = tournamentTeam.hashCode();
    result = 31 * result + score;
    return result;
  }
}
