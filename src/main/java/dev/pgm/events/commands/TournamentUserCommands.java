package dev.pgm.events.commands;

import dev.pgm.events.Events;
import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.format.FormatTournamentImpl;
import dev.pgm.events.format.rounds.veto.VetoRound;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.Optional;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.lib.app.ashcon.intake.parametric.annotation.Text;

public class TournamentUserCommands {

  @Command(aliases = "score", desc = "Shows the current score in the tournament")
  public void currentScore(CommandSender sender, Tournament format) {
    if (format instanceof FormatTournamentImpl) {
      String formatName = ((FormatTournamentImpl) format).getFormatRound().settings().name();
      sender.sendMessage(ChatColor.YELLOW + "For " + formatName + ":");
      sender.sendMessage(format.currentScore().condensed());

      Optional<Tournament> parentOptional = Events.get().getTournamentManager().currentTournament();
      if (parentOptional.isPresent()) {
        sender.sendMessage(ChatColor.YELLOW + "Overall score (excluding " + formatName + "):");
        sender.sendMessage(parentOptional.get().currentScore().condensed());
      }
    } else {
      sender.sendMessage(format.currentScore().condensed());
    }
  }

  @Command(aliases = "veto", desc = "Veto a map")
  public void veto(
      CommandSender sender,
      Match match,
      TournamentTeamManager teamManager,
      Tournament format,
      @Text String option) {
    if (format.currentRound() == null || !(format.currentRound() instanceof VetoRound)) {
      sender.sendMessage(ChatColor.RED + "Veto round is not currently running!");
      return;
    }

    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "Only players can run this command!");
      return;
    }

    Player player = (Player) sender;
    Optional<TournamentTeam> team = teamManager.tournamentTeamPlayer((player).getUniqueId());
    if (!team.isPresent()) {
      sender.sendMessage(ChatColor.RED + "Only players on teams can run this command!");
      return;
    }

    if (!team.get().canVeto(player)) {
      sender.sendMessage(ChatColor.RED + "You are not registered as a vetoer for this team!");
      return;
    }

    try {
      int num = Integer.parseInt(option) - 1;
      VetoRound vetoRound = (VetoRound) format.currentRound();
      if (!vetoRound.validVetoNumber(num)) {
        sender.sendMessage(ChatColor.RED + "That is not a valid veto number: " + (num + 1));
        return;
      }

      if (!vetoRound.picking().equals(team)) {
        sender.sendMessage(ChatColor.RED + "It isn't your turn to veto!");
        return;
      }

      vetoRound.veto(match, team.get(), num);
    } catch (NumberFormatException e) {
      sender.sendMessage(ChatColor.RED + "Invalid argument! Only takes numbers!");
    }
  }

  @Command(aliases = "rounds", desc = "Shows the rounds from this event")
  public void rounds(CommandSender sender, Tournament format) {
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
