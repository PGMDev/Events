package dev.pgm.events.team;

import tc.oc.pgm.match.MatchPlayerImpl;

import java.util.UUID;

public class TournamentPlayerImpl implements TournamentPlayer {

  private final UUID uuid;
  private final boolean canVeto;

  public TournamentPlayerImpl(UUID uuid, boolean canVeto) {
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
    return getUUID().toString();
  }
}
