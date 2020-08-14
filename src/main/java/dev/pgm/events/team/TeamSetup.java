package dev.pgm.events.team;

import java.util.Collection;
import java.util.Map;
import org.bukkit.ChatColor;
import tc.oc.pgm.teams.Team;

public interface TeamSetup {

  Map<TournamentTeam, Team> setup(Collection<Team> teams);

  ChatColor colour(TournamentTeam tournamentTeam);

  Collection<? extends TournamentTeam> teams();
}
