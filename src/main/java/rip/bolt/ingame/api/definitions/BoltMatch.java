package rip.bolt.ingame.api.definitions;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Class to represent a single Bolt match (e.g. a Ranked match).
 * 
 * @author Picajoluna
 */
public class BoltMatch {

    /**
     * @JsonProperty("field") tells JAX-RS what the API name for the variable is, and that the two represent the same thing.
     * You only need to do this if the java variable name differs from the variable name returned by the API.
     * For instance, here, the API returns "match", which below is named "matchId".
     */
    @JsonProperty("match")
    private String matchId; // 6 character hex code representing the match id number

    @JsonProperty(access = Access.WRITE_ONLY) // this is sent to us, but we shouldn't submit (POST) it
    private String map;

    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Team> teams;

    @JsonProperty(access = Access.READ_ONLY) // we need to submit (POST) this
    private List<String> winners = new ArrayList<String>();

    public BoltMatch() {

    }

    public BoltMatch(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchID() {
        return matchId;
    }

    public String getMap() {
        return map;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public List<String> getWinners() {
        return winners;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Match ID: " + getMatchID() + "\n");

        str.append("Teams: ");
        for (int i = 0; i < getTeams().size(); i++)
            str.append(getTeams().get(i).toString() + "\n");

        return str.toString();
    }

}
