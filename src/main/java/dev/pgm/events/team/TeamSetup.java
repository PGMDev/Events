package dev.pgm.events.team;

import java.util.Collection;
import java.util.Map;
import org.bukkit.ChatColor;
import tc.oc.pgm.teams.Team;

/** Links PGM {@link Team}s to {@link TournamentTeam}s */
public interface TeamSetup {

  /**
   * Initiate this setup and return the linked teams
   *
   * @param teams the teams to
   */
  Map<TournamentTeam, Team> setup(Collection<Team> teams);

  /**
   * Gets the color for a team
   *
   * @param tournamentTeam a team
   * @return a color
   */
  ChatColor colour(TournamentTeam tournamentTeam);

  /**
   * Gets all linked teams
   *
   * @return all linked teams
   */
  Collection<? extends TournamentTeam> teams();
}
