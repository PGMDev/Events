package dev.pgm.events.utils;

import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.teams.Team;

public class Parties {

  public static boolean isFull(Party party) {
    if (party instanceof Team) {
      Team team = (Team) party;

      return team.getSize(false) >= team.getMaxPlayers();
    }

    return false;
  }
}
