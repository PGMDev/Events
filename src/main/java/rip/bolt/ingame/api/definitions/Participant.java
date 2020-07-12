package rip.bolt.ingame.api.definitions;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import rip.bolt.ingame.team.TournamentPlayer;

/**
 * Class to represent a single Bolt participant (i.e. player).
 * 
 * @author Picajoluna
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant implements TournamentPlayer {

    private UUID uuid;

    @JsonIgnore
    private boolean canVeto;

    public Participant() {

    }

    public Participant(UUID uuid, boolean canVeto) {
        this.uuid = uuid;
        this.canVeto = canVeto;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean canVeto() {
        return canVeto;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getUUID());

        return str.toString();
    }

}