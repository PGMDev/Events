package dev.pgm.events.commands;

import dev.pgm.events.TournamentManager;
import dev.pgm.events.api.teams.TournamentTeamRegistry;
import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.xml.MapFormatXMLParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
import tc.oc.pgm.util.Audience;

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
    Audience.get(sender).sendMessage(Component.text("Starting tournament.", NamedTextColor.GOLD));
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
      if (bukkit != null && Integration.isVanished(bukkit))
        Integration.setVanished(mp, false, false);
    }

    teamManager.addTeam(team);
    Audience.get(sender)
        .sendMessage(Component.text("Added team " + team.getName() + "!", NamedTextColor.YELLOW));
  }

  @Command("list")
  @CommandDescription("List all loaded teams")
  @Permission("events.staff")
  public void list(CommandSender sender, TournamentTeamRegistry registry) {
    Audience audience = Audience.get(sender);
    audience.sendMessage(Component.text("------- ", NamedTextColor.GOLD)
        .append(Component.text("Registered Teams", NamedTextColor.AQUA))
        .append(Component.text(" -------", NamedTextColor.GOLD)));
    for (TournamentTeam team : registry.getTeams())
      audience.sendMessage(
          Component.text("- ", NamedTextColor.AQUA).append(Component.text(team.getName())));
    audience.sendMessage(
        Component.text("Run /tourney info <team> to see player roster!", NamedTextColor.YELLOW));
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

    Audience audience = Audience.get(sender);
    audience.sendMessage(Component.text("------- ", NamedTextColor.GOLD)
        .append(Component.text(team.getName(), NamedTextColor.AQUA))
        .append(Component.text(" -------", NamedTextColor.GOLD)));
    for (TournamentPlayer player : team.getPlayers()) {
      String playerName = player.getUUID().toString() + " (player hasn't logged on)";
      OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUUID());
      if (offline.getName() != null) playerName = offline.getName();

      audience.sendMessage(
          Component.text("- ", NamedTextColor.AQUA).append(Component.text(playerName)));
    }
  }

  @Command("unregisterall")
  @CommandDescription("Clear all registered teams")
  @Permission("events.staff")
  public void clear(CommandSender sender, TournamentTeamManager teamManager) {
    teamManager.clear();
    Audience.get(sender)
        .sendMessage(Component.text("Unregistered all teams!", NamedTextColor.YELLOW));
  }
}
