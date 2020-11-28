package dev.pgm.events.api.teams;

import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import java.util.List;
import java.util.stream.Collectors;

// this should only be created when registering teams
public class ReferenceTournamentTeam implements TournamentTeam {

  private final String teamName;
  private final TournamentTeamRegistry teamRegistry;

  private TournamentTeam cachedTeam;

  public ReferenceTournamentTeam(
      TournamentTeam tournamentTeam, TournamentTeamRegistry teamRegistry) {
    this.teamName = tournamentTeam.getName();
    this.teamRegistry = teamRegistry;
    this.cachedTeam = tournamentTeam;
  }

  @Override
  public String getName() {
    return teamName;
  }

  @Override
  public List<TournamentPlayer> getPlayers() {
    return updateReference().getPlayers();
  }

  private TournamentTeam updateReference() {
    TournamentTeam team = teamRegistry.findExact(teamName);
    if (team != null) {
      cachedTeam = team;
    }
    return cachedTeam;
  }

  @Override
  public String toString() {
    return "Team "
        + getName()
        + ": "
        + getPlayers().stream().map(x -> x.getUUID().toString()).collect(Collectors.joining(", "));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TournamentTeam that = (TournamentTeam) o;

    return teamName.equals(that.getName());
  }

  @Override
  public int hashCode() {
    return teamName.hashCode();
  }
}
