package dev.pgm.events.api.teams;

import dev.pgm.events.team.TournamentTeam;
import java.util.List;

public interface TournamentTeamFetcher {

  List<? extends TournamentTeam> getTeams();
}
