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
    if (!event
        .getMatch()
        .hasModule(
            TeamMatchModule
                .class)) // shouldn't really ever happen, maybe ffa? just ignore that game
    return;

    Collection<Team> teams = event.getMatch().getModule(TeamMatchModule.class).getTeams();
    teamManager.setupTeams(teams);
  }
}
