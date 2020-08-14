package rip.bolt.ingame.api.definitions;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import rip.bolt.ingame.team.TournamentPlayer;
import rip.bolt.ingame.team.TournamentTeam;

/**
 * Class to represent a given team in a Bolt match.
 * The team will have a list of {@link Participant}s (aka the team roster) assigned to it.
 * This list of players is fetched from the Bolt API in the {@link rip.bolt.ingame.api.APIManager} class.
 * 
 * @author Picajoluna
 */
public class Team implements TournamentTeam {

    private String name;

    @JsonProperty("players")
    private List<TournamentPlayer> participants;

    public Team() {

    }

    public Team(String name, List<TournamentPlayer> participants) {
        this.name = name;
        this.participants = participants;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TournamentPlayer> getPlayers() {
        return this.participants;
    }

    @Override
    public void addPlayer(TournamentPlayer player) {
        this.participants.add(player);
    }

    @Override
    public void removePlayer(UUID uuid) {
        getPlayers().removeIf(p -> p.getUUID().equals(uuid));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Team " + getName() + ": ");

        // Print list of participants on the team
        for (TournamentPlayer player : getPlayers())
                str.append(player.toString() + ", ");
        str.setLength(str.length() - 2); // remove the final ", "

        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TournamentTeam that = (TournamentTeam) o;

        return name.equals(that.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
