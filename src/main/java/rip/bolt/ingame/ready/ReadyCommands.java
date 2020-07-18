package rip.bolt.ingame.ready;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import rip.bolt.ingame.config.AppData;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.match.ObservingParty;
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

        if (!preConditions(match))
            return;

        if (!canReady(sender, player))
            return;

        if (readyParties.isReady(player.getParty())) {
            sender.sendMessage(ChatColor.RED + "You are already ready!");
            return;
        }

        Party party = player.getParty();
        Bukkit.broadcastMessage(party.getColor() + player.getParty().getNameLegacy() + ChatColor.RESET + " is now ready.");
        readyParties.ready(party);

        if (readyParties.allReady(match))
            match.needModule(StartMatchModule.class).forceStartCountdown(Duration.ofSeconds(20), Duration.ZERO);
    }

    @Command(aliases = "unready", desc = "Mark your team as no longer being ready")
    public void unreadyCommand(CommandSender sender, Match match, MatchPlayer player) {
        readyParties.preconditionsCheckMatch(match);

        if (!preConditions(match))
            return;

        if (!canReady(sender, player))
            return;

        if (!readyParties.isReady(player.getParty())) {
            sender.sendMessage(ChatColor.RED + "You are already unready!");
            return;
        }

        Party party = player.getParty();
        Bukkit.broadcastMessage(party.getColor() + player.getParty().getNameLegacy() + ChatColor.RESET + " is now unready.");

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

    private boolean preConditions(Match match) {
        return !match.isRunning() && !match.isFinished();
    }

    private boolean canReady(CommandSender sender, MatchPlayer player) {
        if (!readySystem.canReadyAction()) {
            sender.sendMessage(ChatColor.RED + "You are not able to ready at this time!");
            return false;
        }

        if (!AppData.observersMustReady() && player.getParty() instanceof ObservingParty) {
            sender.sendMessage(ChatColor.RED + "Observers are not allowed to ready!");
            return false;
        }

        return !(player.getParty() instanceof ObservingParty) || sender.hasPermission("ingame.staff");
    }

}
