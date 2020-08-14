package dev.pgm.events.api.definitions;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.pgm.events.api.APIManager;
import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import java.util.List;

/**
 * Class to represent a given team in a match. The team will have a list of {@link Participant}s
 * (aka the team roster) assigned to it. This list of players is fetched from the API in the {@link
 * APIManager} class.
 *
 * @author Picajoluna
 */
public class Team implements TournamentTeam {

  private String name;

  @JsonProperty("players")
  private List<TournamentPlayer> participants;

  public Team() {}

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
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("Team " + getName() + ": ");

    // Print list of participants on the team
    for (TournamentPlayer player : getPlayers()) str.append(player.toString() + ", ");
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
