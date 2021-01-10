package dev.pgm.events.ready;

import dev.pgm.events.config.AppData;
import java.time.Duration;
import java.util.stream.Stream;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.match.ObserverParty;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.start.StartMatchModule;

public class ReadyCommands {

  private final ReadyParties readyParties;
  private final ReadySystem readySystem;

  public ReadyCommands(ReadySystem readySystem, ReadyParties readyParties) {
    this.readyParties = readyParties;
    this.readySystem = readySystem;
  }

  @Command(aliases = "ready", desc = "Ready up")
  public void readyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyParties.preconditionsCheckMatch(match);

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (readyParties.isReady(player.getParty())) {
      sender.sendMessage(ChatColor.RED + "You are already ready!");
      return;
    }

    Party party = player.getParty();
    Bukkit.broadcastMessage(
        party.getColor()
            + player.getParty().getNameLegacy()
            + ChatColor.RESET
            + " is "
            + ChatColor.GREEN
            + "now ready");
    readyParties.ready(party);

    if (readyParties.allReady(match))
      match
          .needModule(StartMatchModule.class)
          .forceStartCountdown(Duration.ofSeconds(20), Duration.ZERO);
  }

  @Command(aliases = "unready", desc = "Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, Match match, MatchPlayer player) {
    readyParties.preconditionsCheckMatch(match);

    if (!preConditions(match)) return;

    if (!canReady(sender, player)) return;

    if (!readyParties.isReady(player.getParty())) {
      sender.sendMessage(ChatColor.RED + "You are already unready!");
      return;
    }

    Party party = player.getParty();
    Bukkit.broadcastMessage(
        party.getColor()
            + player.getParty().getNameLegacy()
            + ChatColor.RESET
            + " is "
            + ChatColor.RED
            + "no longer ready");

    if (readyParties.allReady(match)) {
      readyParties.unReady(party);
      if (readySystem.unreadyShouldCancel()) {
        // check if unready should cancel
        match.getCountdown().cancelAll(StartCountdown.class);
      }
    } else {
      readyParties.unReady(party);
    }
  }

  @Command(aliases = "status", desc = "Display if teams are ready")
  public void status(CommandSender sender, Match match) {
    Stream<? extends Party> parties = match.getCompetitors().stream();
    if (AppData.observersMustReady())
      parties = Stream.concat(Stream.of(match.getDefaultParty()), parties);

    parties
        .map(
            p ->
                p.getColor()
                    + p.getNameLegacy()
                    + ChatColor.RESET
                    + " is "
                    + (readyParties.isReady(p)
                        ? ChatColor.GREEN + "ready"
                        : ChatColor.RED + "not ready"))
        .forEach(sender::sendMessage);
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
