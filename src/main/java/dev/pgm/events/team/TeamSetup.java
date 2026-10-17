package dev.pgm.events.team;

import java.util.Collection;
import java.util.Map;
import net.kyori.adventure.text.format.NamedTextColor;
import tc.oc.pgm.teams.Team;

public interface TeamSetup {

  Map<TournamentTeam, Team> setup(Collection<Team> teams);

  NamedTextColor colour(TournamentTeam tournamentTeam);

  Collection<? extends TournamentTeam> teams();
}
