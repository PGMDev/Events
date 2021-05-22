package dev.pgm.events.commands;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.format.FormatTournamentImpl;
import java.util.Optional;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.lib.app.ashcon.intake.Command;

public class TournamentUserCommands {

  @Command(aliases = "score", desc = "Shows the current score in the tournament")
  public void currentScore(CommandSender sender, TournamentFormat format) {
    if (format instanceof FormatTournamentImpl) {
      String formatName = ((FormatTournamentImpl) format).getFormatRound().settings().name();
      sender.sendMessage(ChatColor.YELLOW + "For " + formatName + ":");
      sender.sendMessage(format.currentScore().condensed());

      Optional<TournamentFormat> parentOptional =
          EventsPlugin.get().getTournamentManager().currentTournament();
      if (parentOptional.isPresent()) {
        sender.sendMessage(ChatColor.YELLOW + "Overall score (excluding " + formatName + "):");
        sender.sendMessage(parentOptional.get().currentScore().condensed());
      }
    } else {
      sender.sendMessage(format.currentScore().condensed());
    }
  }

  @Command(aliases = "rounds", desc = "Shows the rounds from this event")
  public void rounds(CommandSender sender, TournamentFormat format) {
    String header = "Event Rounds";
    if (format instanceof FormatTournamentImpl)
      header += " (" + ((FormatTournamentImpl) format).getFormatRound().settings().name() + ")";

    sender.sendMessage(
        ChatColor.GOLD + "------- " + ChatColor.AQUA + header + ChatColor.GOLD + " -------");
    int round = 1;
    for (RoundDescription roundDescription : format.roundsInformation()) {
      String roundString = ChatColor.GOLD + Integer.toString(round) + ". ";
      TextComponent roundPart = new TextComponent(roundString);
      sender.sendMessage(roundPart, roundDescription.roundInfo());
      round++;
    }
  }
}
