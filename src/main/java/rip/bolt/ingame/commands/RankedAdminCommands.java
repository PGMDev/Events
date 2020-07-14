package rip.bolt.ingame.commands;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import rip.bolt.ingame.RankedManager;
import rip.bolt.ingame.Tournament;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.lib.app.ashcon.intake.CommandException;

public class RankedAdminCommands {

    @Command(aliases = "poll", desc = "Poll the API for a new game", perms = "ingame.staff")
    public void poll(CommandSender sender, Match match) throws CommandException {
        if (match.getPhase() == MatchPhase.RUNNING)
            throw new CommandException(ChatColor.RED + "You may not run this command while a game is running!");

        RankedManager ranked = Tournament.get().getRankedManager();
        if (ranked == null)
            throw new CommandException(ChatColor.RED + "You are not in a ranked server!");

        ranked.poll();
    }

}
