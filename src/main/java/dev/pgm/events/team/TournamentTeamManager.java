package dev.pgm.events.team;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.ChatColor;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.teams.Team;

public interface TournamentTeamManager {

  void addTeam(TournamentTeam team);

  void clear();

  void setupTeams(Collection<Team> teams);

  /**
   * Gets the team the player should be on
   *
   * @param player the player
   * @return the team that the player should be on
   */
  Optional<Team> playerTeam(UUID player);

  Optional<TournamentTeam> tournamentTeamPlayer(UUID player);

  TournamentTeam tournamentTeam(String name);

  Optional<TournamentTeam> tournamentTeam(Competitor team);

  Optional<TournamentTeam> fromTeamID(String id);

  Optional<Team> fromTournamentTeam(TournamentTeam tournamentTeam);

  ChatColor teamColour(TournamentTeam tournamentTeam);

  default String formattedName(Competitor team) {
    return tournamentTeam(team).map(this::formattedName).orElse("NULL");
  }

  String formattedName(TournamentTeam tournamentTeam);

  Collection<? extends TournamentTeam> teams();

  void syncTeams();
}
