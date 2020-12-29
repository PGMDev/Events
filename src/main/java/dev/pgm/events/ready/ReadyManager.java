package dev.pgm.events.ready;

import java.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.start.StartMatchModule;

public class ReadyManager {

  private final ReadyParties readyParties;
  private final ReadySystem readySystem;

  public ReadyManager(ReadySystem readySystem, ReadyParties readyParties) {
    this.readySystem = readySystem;
    this.readyParties = readyParties;
  }

  public void createMatchStart(Match match) {
    createMatchStart(match, Duration.ofSeconds(20));
  }

  public void createMatchStart(Match match, Duration duration) {
    match.needModule(StartMatchModule.class).forceStartCountdown(duration, Duration.ZERO);
  }

  public void cancelMatchStart(Match match) {
    match.getCountdown().cancelAll(StartCountdown.class);
  }

  public void readyTeam(Party party) {
    if (party.isNamePlural()) {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " are now ready.");
    } else {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " is now ready.");
    }

    readyParties.ready(party);

    Match match = party.getMatch();
    if (readyParties.allReady(match)) {
      createMatchStart(match);
    }
  }

  public void unreadyTeam(Party party) {
    if (party.isNamePlural()) {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " are now unready.");
    } else {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " is now unready.");
    }

    Match match = party.getMatch();
    if (readyParties.allReady(match)) {
      readyParties.unReady(party);
      if (readySystem.unreadyShouldCancel()) {
        // check if unready should cancel
        cancelMatchStart(party.getMatch());
      }
    } else {
      readyParties.unReady(party);
    }
  }
}
