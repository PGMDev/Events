package dev.pgm.events.ready;

import dev.pgm.events.config.AppData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.match.ObserverParty;

public class ReadyCommands {

  private final ReadyManager readyManager;
  private final ReadyParties readyParties;
  private final ReadySystem readySystem;

  public ReadyCommands(
      ReadyManager readyManager, ReadySystem readySystem, ReadyParties readyParties) {
    this.readyManager = readyManager;
    this.readyParties = readyParties;
    this.readySystem = readySystem;
  }

  @Command(aliases = "ready", desc = "Ready up")
  public void readyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyParties.preconditionsCheckMatch(match);
    Party party = player.getParty();

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (readyParties.isReady(party)) {
      sender.sendMessage(ChatColor.RED + "You are already ready!");
      return;
    }

    if (AppData.readyFullTeamRequired() && !readyParties.isFull(party)) {
      sender.sendMessage(ChatColor.RED + "You can not ready until your team is full!");
      return;
    }

    readyManager.readyTeam(party);
  }

  @Command(aliases = "unready", desc = "Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyParties.preconditionsCheckMatch(match);
    Party party = player.getParty();

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (!readyParties.isReady(party)) {
      sender.sendMessage(ChatColor.RED + "You are already unready!");
      return;
    }

    if (readyParties.allReady(match)) {
      readyManager.unreadyTeam(party);
      if (readySystem.unreadyShouldCancel()) {
        // check if unready should cancel
        readyManager.cancelMatchStart(match);
      }
    } else {
      readyManager.readyTeam(party);
    }
  }

  private boolean preConditions(Match match) {
    return !match.isRunning() && !match.isFinished();
  }

  private boolean canReady(CommandSender sender, MatchPlayer player) {
    if (!readySystem.canReadyAction()) {
      sender.sendMessage(ChatColor.RED + "You are not able to ready at this time!");
      return false;
    }

    if (!AppData.observersMustReady() && player.getParty() instanceof ObserverParty) {
      sender.sendMessage(ChatColor.RED + "Observers are not allowed to ready!");
      return false;
    }

    return !(player.getParty() instanceof ObserverParty) || sender.hasPermission("events.staff");
  }
}
