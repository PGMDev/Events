package dev.pgm.events.ready;

import java.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.teams.Team;

public class ReadyManagerImpl implements ReadyManager {

  private final ReadyParties parties;
  private final ReadySystem system;

  public ReadyManagerImpl(ReadySystem system, ReadyParties parties) {
    this.system = system;
    this.parties = parties;
  }

  @Override
  public void createMatchStart(Match match, Duration duration) {
    match.needModule(StartMatchModule.class).forceStartCountdown(duration, Duration.ZERO);
  }

  @Override
  public void cancelMatchStart(Match match) {
    match.getCountdown().cancelAll(StartCountdown.class);
  }

  @Override
  public void readyTeam(Party party) {
    if (party.isNamePlural()) {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " are now ready.");
    } else {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " is now ready.");
    }

    parties.ready(party);

    Match match = party.getMatch();
    if (parties.allReady(match)) {
      createMatchStart(match);
    }
  }

  @Override
  public void unreadyTeam(Party party) {
    if (party.isNamePlural()) {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " are now unready.");
    } else {
      Bukkit.broadcastMessage(
          party.getColor() + party.getNameLegacy() + ChatColor.RESET + " is now unready.");
    }

    Match match = party.getMatch();
    if (parties.allReady(match)) {
      parties.unReady(party);
      if (system.unreadyShouldCancel()) {
        // check if unready should cancel
        cancelMatchStart(party.getMatch());
      }
    } else {
      parties.unReady(party);
    }
  }

  @Override
  public boolean isReady(Party party) {
    return parties.isReady(party);
  }

  @Override
  public boolean allReady(Match match) {
    return parties.allReady(match);
  }

  @Override
  public boolean unreadyShouldCancel() {
    return system.unreadyShouldCancel();
  }

  @Override
  public boolean canReadyAction() {
    return system.canReadyAction();
  }

  @Override
  public void reset() {
    parties.reset();
    system.reset();
  }

  @Override
  public Duration cancelDuration(Match match) {
    return system.onCancel(this.allReady(match));
  }

  @Override
  public void onStart(Match match, Duration duration) {
    system.onStart(
            duration,
            this.allReady(match));
  }
}
