package dev.pgm.events.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.veto.VetoRound;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.match.Match;

public class VetoCommands extends BaseCommand {

  @Dependency private TournamentTeamManager teamManager;

  @CommandAlias("veto")
  @Description("Veto a map")
  public void veto(CommandSender sender, Match match, TournamentFormat format, String option) {
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
}
