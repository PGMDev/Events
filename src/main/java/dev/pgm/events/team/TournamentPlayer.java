package dev.pgm.events.team;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.pgm.events.api.definitions.Participant;
import java.util.UUID;

@JsonDeserialize(as = Participant.class)
public interface TournamentPlayer {

  public UUID getUUID();

  public boolean canVeto();

  public static TournamentPlayer create(UUID uuid, boolean canVeto) {
    return new Participant(uuid, canVeto);
  }
}
