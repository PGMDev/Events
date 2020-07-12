package rip.bolt.ingame.team;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import rip.bolt.ingame.api.definitions.Participant;

@JsonDeserialize(as = Participant.class)
public interface TournamentPlayer {

    public UUID getUUID();

    public boolean canVeto();

    public static TournamentPlayer create(UUID uuid, boolean canVeto) {
        return new Participant(uuid, canVeto);
    }

}
