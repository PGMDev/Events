package dev.pgm.events.team;

import java.util.UUID;

public interface TournamentPlayer {

  UUID getUUID();

  boolean canVeto();

  static TournamentPlayer create(UUID uuid, boolean canVeto) {
    return new DefaultTournamentPlayer(uuid, canVeto);
  }
}
