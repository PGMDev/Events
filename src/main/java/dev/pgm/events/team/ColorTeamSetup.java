package dev.pgm.events.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.format.NamedTextColor;
import tc.oc.pgm.teams.Team;

public class ColorTeamSetup implements TeamSetup {

  private final List<TournamentTeam> currentTeams;
  // not active at the moment
  private final Set<TournamentTeam> unassigned;
  private final Map<NamedTextColor, TournamentTeam> colorTeams = new HashMap<>();

  // active at the moment
  private final Map<TournamentTeam, Team> assigned = new IdentityHashMap<>();

  public ColorTeamSetup(Collection<? extends TournamentTeam> tournamentTeams) {
    this.currentTeams = new ArrayList<>(tournamentTeams);
    this.unassigned = new LinkedHashSet<>(tournamentTeams);
  }

  @Override
  public Collection<? extends TournamentTeam> teams() {
    return currentTeams;
  }

  @Override
  public NamedTextColor colour(TournamentTeam tournamentTeam) {
    if (assigned.containsKey(tournamentTeam)) {
      return assigned.get(tournamentTeam).getTextColor();
    }
    for (Map.Entry<NamedTextColor, TournamentTeam> entry : colorTeams.entrySet()) {
      if (entry.getValue().equals(tournamentTeam)) {
        return entry.getKey();
      }
    }
    return NamedTextColor.WHITE;
  }

  @Override
  public Map<TournamentTeam, Team> setup(Collection<Team> teams) {
    reset();
    assignTeams(teams);
    return assigned;
  }

  private void assignTeams(Collection<Team> teams) {
    List<Team> unassignedTeams = new ArrayList<>();

    for (Team team : teams) {
      TournamentTeam colourTeam = colorTeams.get(team.getTextColor());
      if (colourTeam != null && !this.assigned.containsKey(colourTeam)) {
        // team with that colour, lets assign the
        assignTeam(team, colourTeam);
        continue;
      }

      // no team with that colour, time to do a bit of soul-searching and add the team to the cool
      // list
      unassignedTeams.add(team);
    }
    // unassign all teams still with colours
    unassignColourTeams();
    assignLeftovers(unassignedTeams);
  }

  private void assignTeam(Team team, TournamentTeam tournamentTeam) {
    assigned.put(tournamentTeam, team);
    team.setName(tournamentTeam.getName());
  }

  private void unassignColourTeams() {
    currentTeams.stream()
        .filter(colorTeams::containsValue)
        .filter(x -> !assigned.containsKey(x))
        .forEach(unassigned::add);
  }

  private void assignLeftovers(List<Team> leftoverTeams) {
    Iterator<TournamentTeam> teamIterator = unassigned.iterator();
    for (Team leftover : leftoverTeams) {
      if (unassigned.isEmpty()) {
        return;
      }

      while (teamIterator.hasNext()) {
        TournamentTeam selected = teamIterator.next();
        if (!assigned.containsKey(selected)) {
          assignTeam(leftover, selected);
          break;
        }
      }
    }

    assigned.keySet().forEach(unassigned::remove);
  }

  private void reset() {
    for (TournamentTeam tournamentTeam : assigned.keySet()) {
      Team team = assigned.get(tournamentTeam);
      TournamentTeam deleted = colorTeams.put(team.getTextColor(), tournamentTeam);

      // there should never be a duplicate but just in case remove it here
      if (deleted != null && !deleted.equals(tournamentTeam)) {
        // team used to have this colour, doesn't anymore
        unassigned.add(deleted);
      }
    }

    assigned.clear();
  }
}
