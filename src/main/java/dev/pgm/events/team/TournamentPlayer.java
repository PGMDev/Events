package dev.pgm.events.team;

import tc.oc.pgm.api.player.MatchPlayer;

import java.util.UUID;

/** Represents a single participating player */
public interface TournamentPlayer {

  /**
   * Gets the UUID of this player
   *
   * @return The UUID of this player
   */
  UUID getUUID();

  /**
   * Gets if this player can veto maps (/veto)
   *
   * @return If this player can veto
   */
  boolean canVeto();

  /**
   * Creates a new {@link TournamentPlayer}
   *
   * @param uuid A player uuid
   * @param canVeto Whether this player can veto maps
   * @return An instance of this
   */
  static TournamentPlayer create(UUID uuid, boolean canVeto) {
    return new TournamentPlayerImpl(uuid, canVeto);
  }
}
