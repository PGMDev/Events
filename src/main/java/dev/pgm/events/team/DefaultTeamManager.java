package dev.pgm.events.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.ChatColor;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.teams.Team;

public class DefaultTeamManager implements TournamentTeamManager {

  private final List<TournamentTeam> teams;

  private TeamSetup teamSetup;
  private Map<TournamentTeam, Team> teamMap;

  public DefaultTeamManager(TeamSetup teamSetup) {
    this.teamSetup = teamSetup;
    this.teams = new ArrayList<>();
    teamMap = new HashMap<>();
  }

  public DefaultTeamManager() {
    this(new ColorTeamSetup(new ArrayList<>()));
  }

  public static TournamentTeamManager manager() {
    return new DefaultTeamManager(new ColorTeamSetup(new ArrayList<TournamentTeam>()));
  }

  @Override
  public void addTeam(TournamentTeam team) {
    if (this.teams.contains(team)) return;

    this.teams.add(team);
    this.teamSetup = new ColorTeamSetup(teams);
  }

  @Override
  public void clear() {
    this.teams.clear();
    this.teamSetup = new ColorTeamSetup(teams);
  }

  @Override
  public Optional<TournamentTeam> tournamentTeamPlayer(UUID player) {
    for (TournamentTeam team : teamMap.keySet())
      if (team.containsPlayer(player)) return Optional.of(team);

    return Optional.empty();
  }

  @Override
  public void setupTeams(Collection<Team> teams) {
    teamMap = teamSetup.setup(teams);
  }

  @Override
  public Optional<Team> playerTeam(UUID player) {
    for (TournamentTeam tournamentTeam : teamMap.keySet())
      if (tournamentTeam.containsPlayer(player)) return Optional.of(teamMap.get(tournamentTeam));

    return Optional.empty();
  }

  @Override
  public TournamentTeam tournamentTeam(String name) {
    for (TournamentTeam tournamentTeam : teamMap.keySet())
      if (tournamentTeam.getName().equalsIgnoreCase(name)) return tournamentTeam;

    return null;
  }

  @Override
  public Optional<TournamentTeam> tournamentTeam(Competitor team) {
    return fromTeamID(team.getId());
  }

  @Override
  public Optional<TournamentTeam> fromTeamID(String id) {
    for (Map.Entry<TournamentTeam, Team> entry : teamMap.entrySet())
      if (entry.getValue().getId().equals(id)) return Optional.of(entry.getKey());

    return Optional.empty();
  }

  @Override
  public Optional<Team> fromTournamentTeam(TournamentTeam tournamentTeam) {
    for (Map.Entry<TournamentTeam, Team> entry : teamMap.entrySet())
      if (entry.getKey().equals(tournamentTeam)) return Optional.of(entry.getValue());

    return Optional.empty();
  }

  @Override
  public ChatColor teamColour(TournamentTeam tournamentTeam) {
    return teamSetup.colour(tournamentTeam);
  }

  @Override
  public String formattedName(TournamentTeam tournamentTeam) {
    return teamColour(tournamentTeam) + tournamentTeam.getName();
  }

  @Override
  public Collection<? extends TournamentTeam> teams() {
    return teamSetup.teams();
  }

  @Override
  public void syncTeams() {
    List<MatchPlayer> unassigned = new ArrayList<>();

    teams.forEach(
        tournamentTeam -> {
          // Get the matching team if playing
          Optional<Team> matchTeam = fromTournamentTeam(tournamentTeam);
          matchTeam.ifPresent(
              team -> {
                List<? extends TournamentPlayer> toAssign =
                    new ArrayList<>(tournamentTeam.getPlayers());
                team.getPlayers()
                    .forEach(
                        player -> {
                          // If not on team move to unassigned
                          if (!tournamentTeam.containsPlayer(player.getId())) {
                            unassigned.add(player);
                          } else {
                            toAssign.removeIf(
                                tournamentPlayer ->
                                    tournamentPlayer.getUUID().equals(player.getId()));
                          }
                        });

                // Find other players on server to assign
                toAssign.forEach(
                    tournamentPlayer -> {
                      MatchPlayer player = team.getMatch().getPlayer(tournamentPlayer.getUUID());
                      if (player != null) {
                        player.getMatch().setParty(player, team);
                        unassigned.remove(player);
                      }
                    });
              });
        });

    // Move all unassigned players to obs
    unassigned.forEach(
        player -> player.getMatch().setParty(player, player.getMatch().getDefaultParty()));
  }
}
