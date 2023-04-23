package dev.pgm.events.utils;

import java.util.UUID;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.blitz.BlitzMatchModule;
import tc.oc.pgm.teams.Team;

public class JoinUtils {

  public static Response canJoin(UUID uuid, Party party) {
    if (isPartyFull(party)) return Response.deny("You team is full!");
    if (!canJoinBlitz(uuid, party.getMatch()))
      return Response.deny("You may not join a blitz match in progress!");

    return Response.allow();
  }

  public static boolean isPartyFull(Party party) {
    if (party instanceof Team) {
      Team team = (Team) party;
      return team.getSize() >= team.getMaxPlayers();
    }
    return false;
  }

  public static boolean canJoinBlitz(UUID uuid, Match match) {
    return match
        .moduleOptional(BlitzMatchModule.class)
        .map(bmm -> canJoinBlitz(bmm, uuid, match))
        .orElse(true);
  }

  // Mimics PGM behavior for BlitzMatchModule#canJoin, but uuid as player to run this before login
  private static boolean canJoinBlitz(BlitzMatchModule bmm, UUID uuid, Match match) {
    if (bmm.isPlayerEliminated(uuid)) return false;
    return !match.isRunning() || bmm.getConfig().getJoinFilter().query(match).isAllowed();
  }

  public static boolean isBlitzEliminated(MatchPlayer player) {
    return player
        .getMatch()
        .moduleOptional(BlitzMatchModule.class)
        .map(bmm -> bmm.isPlayerEliminated(player.getId()))
        .orElse(false);
  }
}
