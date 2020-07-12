package rip.bolt.ingame.team;

import org.bukkit.ChatColor;
import tc.oc.pgm.teams.Team;

import java.util.Collection;
import java.util.Map;

public interface TeamSetup {

    Map<TournamentTeam, Team> setup(Collection<Team> teams);

    ChatColor colour(TournamentTeam tournamentTeam);

    Collection<? extends TournamentTeam> teams();
}
