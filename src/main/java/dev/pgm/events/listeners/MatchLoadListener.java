package dev.pgm.events.listeners;

import dev.pgm.events.team.TournamentTeamManager;
import java.util.Collection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.teams.Team;
import tc.oc.pgm.teams.TeamMatchModule;

public class MatchLoadListener implements Listener {

  private final TournamentTeamManager teamManager;

  public MatchLoadListener(TournamentTeamManager teamManager) {
    this.teamManager = teamManager;
  }

  @EventHandler
  public void onLoad(MatchLoadEvent event) {
    var tmm = event.getMatch().getModule(TeamMatchModule.class);
    if (tmm == null) return;

    Collection<Team> teams = tmm.getTeams();
    if (teams.isEmpty()) return;

    teamManager.setupTeams(teams);
  }
}
