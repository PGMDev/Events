package rip.bolt.ingame.team;

import org.bukkit.ChatColor;
import tc.oc.pgm.teams.Team;

import java.util.*;

public class ColorTeamSetup implements TeamSetup {

    private final Collection<? extends TournamentTeam> currentTeams;
    //not active at the moment
    private final Set<TournamentTeam> unassigned;
    private final Map<ChatColor, TournamentTeam> colorTeams = new HashMap<>();

    //active at the moment
    private final Map<TournamentTeam, Team> assigned = new HashMap<>();

    public ColorTeamSetup(Collection<? extends TournamentTeam> tournamentTeams) {
        unassigned = new HashSet<>();
        unassigned.addAll(tournamentTeams);
        this.currentTeams = tournamentTeams;
    }

    @Override
    public Collection<? extends TournamentTeam> teams() {
        return currentTeams;
    }

    @Override
    public ChatColor colour(TournamentTeam tournamentTeam) {
        if (assigned.containsKey(tournamentTeam)) {
            return assigned.get(tournamentTeam).getColor();
        }
        for (ChatColor colour : colorTeams.keySet()) {
            if (colorTeams.get(colour).equals(tournamentTeam)) {
                return colour;
            }
        }
        return ChatColor.WHITE;
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
            TournamentTeam colourTeam = colorTeams.get(team.getColor());
            if (colourTeam != null) {
                //team with that colour, lets assign the
                assignTeam(team, colourTeam);
                //colorTeams.remove(team.getColor());
                continue;
            }

            //no team with that colour, time to do a bit of soul searching and add the team to the cool list
            unassignedTeams.add(team);
        }
        //unassign all teams still with colours
        unassignColourTeams();
        assignLeftovers(unassignedTeams);
    }

    private void assignTeam(Team team, TournamentTeam tournamentTeam) {
        assigned.put(tournamentTeam, team);
        team.setName(tournamentTeam.getName());
    }

    private void unassignColourTeams() {
        colorTeams.values().stream()
                .filter(Objects::nonNull)
                .filter(x -> !assigned.containsKey(x))
                .forEach(unassigned::add);

        //colorTeams.clear();
    }

    private void assignLeftovers(List<Team> leftoverTeams) {
        for (Team leftover : leftoverTeams) {
            if (unassigned.isEmpty()) {
                return;
            }

            TournamentTeam selected = unassigned.iterator().next();
            unassigned.remove(selected);
            assignTeam(leftover, selected);
        }
    }

    private void reset() {
        for (TournamentTeam tournamentTeam : assigned.keySet()) {
            Team team = assigned.get(tournamentTeam);
            TournamentTeam deleted = colorTeams.put(team.getColor(), tournamentTeam);

            //there should never be a duplicate but just in case remove it here
            if (deleted != null && !deleted.getName().equals(tournamentTeam.getName())) {
                //team used to have this colour, doesn't anymore
                unassigned.add(deleted);
            }
        }

        assigned.clear();
    }
}
