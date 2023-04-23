package dev.pgm.events.utils;

import java.util.UUID;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.blitz.BlitzMatchModule;
import tc.oc.pgm.teams.Team;

public class JoinUtils {

  public static boolean canJoin(UUID uuid, Party party) {
    return !isPartyFull(party) && canJoinBlitz(uuid, party.getMatch());
  }

  public static boolean isPartyFull(Party party) {
    if (party instanceof Team) {
      Team team = (Team) party;
      return team.getSize() >= team.getMaxPlayers();
    }
    return false;
  }

  public static boolean canJoinBlitz(MatchPlayer player) {
    return canJoinBlitz(player.getId(), player.getMatch());
  }

  public static boolean canJoinBlitz(UUID uuid, Match match) {
    return match
        .moduleOptional(BlitzMatchModule.class)
        .map(bmm -> canJoinBlitz(bmm, uuid, match))
        .orElse(true);
  }

  // Mimics PGM behavior for BlitzMatchModule#canJoin, but uuid as player to run this before login
  public static boolean canJoinBlitz(BlitzMatchModule bmm, UUID uuid, Match match) {
    if (bmm.isPlayerEliminated(uuid)) return false;
    return match.isRunning() && bmm.getConfig().getJoinFilter().query(match).isAllowed();
  }

  public static boolean isBlitzEliminated(MatchPlayer player) {
    return player
        .getMatch()
        .moduleOptional(BlitzMatchModule.class)
        .map(bmm -> bmm.isPlayerEliminated(player.getId()))
        .orElse(false);
  }
}
