package dev.pgm.events.team;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.ChatColor;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.teams.Team;

/** Keeps track of teams participating in the running tournament */
public interface TournamentTeamManager {

  /**
   * Adds a team to the running tournament
   *
   * @param team a team that should participate in the running tournament
   */
  void addTeam(TournamentTeam team);

  /** Remove all teams currently participating */
  void clear();

  /**
   * Tells this manager which {@link Team}s exist in the relevant {@link tc.oc.pgm.api.match.Match}
   * //TODO ugly
   *
   * @param teams the {@link Team}s connected to the current {@link tc.oc.pgm.api.match.Match}
   */
  void setupTeams(Collection<Team> teams);

  /**
   * Gets the team the player should be on
   *
   * @param player a player uuid
   * @return the team that the player should be on
   */
  Optional<Team> playerTeam(UUID player);

  /**
   * Gets the team the player should be on
   *
   * @param player a player uuid
   * @return the team that the player should be on
   */
  Optional<TournamentTeam> tournamentTeamPlayer(UUID player);

  /**
   * Gets a {@link TournamentTeam} with a team.
   *
   * @param team the team
   * @return the team belonging to the given team
   */
  Optional<TournamentTeam> tournamentTeam(Competitor team);

  /**
   * Gets a {@link TournamentTeam} with an id.
   *
   * @param id the team id
   * @return the team with the given id
   */
  Optional<TournamentTeam> fromTeamID(String id);

  /**
   * Gets the color of the given team
   *
   * @param tournamentTeam a team
   * @return a color
   */
  ChatColor teamColour(TournamentTeam tournamentTeam);

  default String formattedName(Competitor team) {
    return tournamentTeam(team).map(this::formattedName).orElse("NULL");
  }

  /**
   * Returns the team name with the team color
   *
   * @param tournamentTeam a team
   * @return the team name decorated with the team color
   */
  String formattedName(TournamentTeam tournamentTeam);

  /**
   * Gets all participating teams
   *
   * @return all participating teams
   */
  Collection<? extends TournamentTeam> teams();
}
