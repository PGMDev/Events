package dev.pgm.events.team;

import dev.pgm.events.utils.JoinUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.ChatColor;
import tc.oc.pgm.api.party.Competitor;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.join.JoinRequest;
import tc.oc.pgm.teams.Team;

public class DefaultTeamManager implements TournamentTeamManager {

  private final List<TournamentTeam> teams;

  private @Nullable TeamSetup teamSetup;
  private Map<TournamentTeam, Team> teamMap;

  public DefaultTeamManager() {
    this.teams = new ArrayList<>();
    this.teamMap = new HashMap<>();
  }

  public static TournamentTeamManager manager() {
    return new DefaultTeamManager();
  }

  @Override
  public void addTeam(TournamentTeam team) {
    if (this.teams.contains(team)) return;

    this.teams.add(team);
    this.teamSetup = null;
  }

  @Override
  public void clear() {
    this.teams.clear();
    this.teamSetup = null;
  }

  @Override
  public Optional<TournamentTeam> tournamentTeamPlayer(UUID player) {
    for (TournamentTeam team : teamMap.keySet())
      if (team.containsPlayer(player)) return Optional.of(team);

    return Optional.empty();
  }

  @Override
  public void setupTeams(Collection<Team> teams) {
    teamMap = getTeamSetup().setup(teams);
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
    return getTeamSetup().colour(tournamentTeam);
  }

  @Override
  public String formattedName(TournamentTeam tournamentTeam) {
    return teamColour(tournamentTeam) + tournamentTeam.getName();
  }

  @Override
  public Collection<? extends TournamentTeam> teams() {
    return getTeamSetup().teams();
  }

  private TeamSetup getTeamSetup() {
    if (teamSetup == null) this.teamSetup = new ColorTeamSetup(this.teams);
    return teamSetup;
  }

  @Override
  public void syncTeams() {
    List<MatchPlayer> unassigned = new ArrayList<>();

    teams.forEach(
        eventsTeam ->
            fromTournamentTeam(eventsTeam)
                .ifPresent(pgmTeam -> syncTeam(eventsTeam, pgmTeam, unassigned)));

    syncObserverPlayers(unassigned);
  }

  private void syncTeam(TournamentTeam eventsTeam, Team pgmTeam, List<MatchPlayer> unassigned) {
    List<TournamentPlayer> toAssign = new ArrayList<>(eventsTeam.getPlayers());
    JoinRequest joinRequest = JoinRequest.of(pgmTeam);

    for (MatchPlayer matchPlayer : pgmTeam.getPlayers()) {
      // Not in team, move to unassigned. Either another team, or end up in obs
      if (!eventsTeam.containsPlayer(matchPlayer.getId())) unassigned.add(matchPlayer);
      else
        toAssign.removeIf(
            tournamentPlayer -> tournamentPlayer.getUUID().equals(matchPlayer.getId()));
    }

    // Move other players to the team (from obs or other teams)
    toAssign.forEach(
        tournamentPlayer -> {
          MatchPlayer player = pgmTeam.getMatch().getPlayer(tournamentPlayer.getUUID());
          if (player != null && JoinUtils.canJoin(player.getId(), pgmTeam)) {
            if (syncPlayer(player, pgmTeam, joinRequest)) {
              unassigned.remove(player);
            }
          }
        });
  }

  private void syncObserverPlayers(List<MatchPlayer> unassigned) {
    JoinRequest request = JoinRequest.of(null, JoinRequest.Flag.FORCE);
    unassigned.forEach(player -> syncPlayer(player, player.getMatch().getDefaultParty(), request));
  }

  private boolean syncPlayer(MatchPlayer player, Party party, JoinRequest request) {
    if (player.getParty() == party) return true;
    return player.getMatch().setParty(player, party, request);
  }
}
