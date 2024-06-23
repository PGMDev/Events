package dev.pgm.events.commands;

import dev.pgm.events.TournamentManager;
import dev.pgm.events.api.teams.TournamentTeamRegistry;
import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.xml.MapFormatXMLParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.integration.Integration;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchManager;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.org.incendo.cloud.annotation.specifier.Greedy;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Argument;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Command;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.CommandDescription;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Permission;

@Command("tourney|tournament|tm|events")
public class TournamentAdminCommands {

  @Command("create <format>")
  @CommandDescription("Creates a tournament")
  @Permission("events.staff")
  public void tourney(
      CommandSender sender,
      TournamentManager manager,
      Match match,
      @Argument("format") @Greedy String pool) {
    manager.createTournament(match, MapFormatXMLParser.parse(pool));
    sender.sendMessage(ChatColor.GOLD + "Starting tournament.");
  }

  @Command("register <team>")
  @CommandDescription("Register a team")
  @Permission("events.staff")
  public void register(
      CommandSender sender,
      TournamentTeamRegistry teamRegistry,
      TournamentTeamManager teamManager,
      @Argument("team") @Greedy String name) {
    TournamentTeam team = teamRegistry.getTeam(name);
    // TODO move to provider
    if (team == null) throw new CommandException("Team not found!");

    MatchManager matchManager = PGM.get().getMatchManager();

    for (TournamentPlayer player : team.getPlayers()) {
      Player bukkit = Bukkit.getPlayer(player.getUUID());
      MatchPlayer mp = matchManager.getPlayer(bukkit);
      if (Integration.isVanished(bukkit)) Integration.setVanished(mp, false, false);
    }

    teamManager.addTeam(team);
    sender.sendMessage(ChatColor.YELLOW + "Added team " + team.getName() + "!");
  }

  @Command("list")
  @CommandDescription("List all loaded teams")
  @Permission("events.staff")
  public void list(CommandSender sender, TournamentTeamRegistry registry) {
    sender.sendMessage(
        ChatColor.GOLD
            + "------- "
            + ChatColor.AQUA
            + "Registered Teams"
            + ChatColor.GOLD
            + " -------");
    for (TournamentTeam team : registry.getTeams())
      sender.sendMessage(ChatColor.AQUA + "- " + team.getName());
    sender.sendMessage(ChatColor.YELLOW + "Run /tourney info <team> to see player roster!");
  }

  @Command("info <team>")
  @CommandDescription("View information about a team")
  @Permission("events.staff")
  public void info(
      CommandSender sender,
      TournamentTeamRegistry registry,
      @Argument("team") @Greedy String name) {
    TournamentTeam team = registry.getTeam(name);
    if (team == null) throw new CommandException("Team not found!");

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

  @Command("unregisterall")
  @CommandDescription("Clear all registered teams")
  @Permission("events.staff")
  public void clear(CommandSender sender, TournamentTeamManager teamManager) {
    teamManager.clear();
    sender.sendMessage(ChatColor.YELLOW + "Unregistered all teams!");
  }
}
