package dev.pgm.events.api.teams;

import dev.pgm.events.team.TournamentTeam;
import java.util.List;

public interface TournamentTeamRegistry {

  // this needs to return the actual tournament team?
  TournamentTeam getTeam(String name);

  TournamentTeam findExact(String name);

  List<? extends TournamentTeam> getTeams();

  void reload();
}
