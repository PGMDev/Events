package dev.pgm.events.api.definitions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.pgm.events.team.TournamentPlayer;
import java.util.UUID;

/**
 * Class to represent a single participant (i.e. player).
 *
 * @author Picajoluna
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant implements TournamentPlayer {

  private UUID uuid;

  @JsonIgnore private boolean canVeto;

  public Participant() {}

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
