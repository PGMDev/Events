package dev.pgm.events.commands;

import dev.pgm.events.TournamentManager;
import dev.pgm.events.team.ConfigTeamParser;
import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.xml.MapFormatXMLParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.lib.app.ashcon.intake.parametric.annotation.Text;

public class TournamentAdminCommands {

  @Command(
      aliases = "create",
      desc = "Creates a tournament",
      usage = "<format>",
      perms = "events.staff")
  public void tourney(
      CommandSender sender, TournamentManager manager, Match match, @Text String pool) {
    manager.createTournament(match, MapFormatXMLParser.parse(pool));
    sender.sendMessage(ChatColor.GOLD + "Starting tournament.");
  }

  @Command(aliases = "register", desc = "Register a team", usage = "<team>", perms = "events.staff")
  public void register(CommandSender sender, TournamentTeamManager teamManager, @Text String name) {
    TournamentTeam team = ConfigTeamParser.getInstance().getTeam(name);
    if (team == null) { // TODO move to provider
      sender.sendMessage(ChatColor.RED + "Team not found!");
      return;
    }

    teamManager.addTeam(team);
    sender.sendMessage(ChatColor.YELLOW + "Added team " + team.getName() + "!");
  }

  @Command(aliases = "list", desc = "List all loaded teams", perms = "events.staff")
  public void list(CommandSender sender) {
    sender.sendMessage(
        ChatColor.GOLD
            + "------- "
            + ChatColor.AQUA
            + "Registered Teams"
            + ChatColor.GOLD
            + " -------");
    for (TournamentTeam team : ConfigTeamParser.getInstance().getTeams())
      sender.sendMessage(ChatColor.AQUA + "- " + team.getName());
    sender.sendMessage(ChatColor.YELLOW + "Run /tourney info <team> to see player roster!");
  }

  @Command(
      aliases = "info",
      desc = "View information about a team",
      usage = "<team",
      perms = "events.staff")
  public void info(CommandSender sender, @Text String name) {
    TournamentTeam team = ConfigTeamParser.getInstance().getTeam(name);
    if (team == null) {
      sender.sendMessage(ChatColor.RED + "Team not found!");
      return;
    }

    sender.sendMessage(
        ChatColor.GOLD
            + "------- "
            + ChatColor.AQUA
            + team.getName()
            + ChatColor.GOLD
            + " -------");
    for (TournamentPlayer player : team.getPlayers()) {
      String playerName =
          player.getUUID().toString() + ChatColor.GRAY + " (player hasn't logged on)";
      OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUUID());
      if (offline.getName() != null) playerName = offline.getName();

      sender.sendMessage(ChatColor.AQUA + "- " + playerName);
    }
  }

  @Command(aliases = "unregisterall", desc = "Clear all registered teams", perms = "events.staff")
  public void clear(CommandSender sender, TournamentTeamManager teamManager) {
    teamManager.clear();
    sender.sendMessage(ChatColor.YELLOW + "Unregistered all teams!");
  }
}
