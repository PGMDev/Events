package dev.pgm.events.commands;

import static net.kyori.adventure.text.Component.text;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import dev.pgm.events.Tournament;
import dev.pgm.events.TournamentManager;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.format.FormatTournamentImpl;
import dev.pgm.events.team.ConfigTeamParser;
import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.xml.MapFormatXMLParser;
import java.util.Optional;
import net.md_5.bungee.api.chat.TextComponent;
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
import tc.oc.pgm.util.Audience;

@CommandAlias("tourney|tournament|tm|events")
public class TournamentCommands extends BaseCommand {

  private static final String STAFF_PERM = "events.staff";

  @Dependency private TournamentManager manager;
  @Dependency private TournamentTeamManager teamManager;

  @Default
  public void commandUsage(CommandSender sender) {
    if (!sender.hasPermission(STAFF_PERM)) {
      Audience.get(sender).sendWarning(text("/tourney <scores|rounds>"));
      return;
    }
    Audience.get(sender)
        .sendWarning(text("/tourney <scores|rounds|create|register|list|info|unregisterall>"));
  }

  @Subcommand("scores")
  @Description("Shows the current score in the tournament")
  public void currentScore(CommandSender sender, TournamentFormat format) {
    if (format instanceof FormatTournamentImpl) {
      String formatName = ((FormatTournamentImpl) format).getFormatRound().settings().name();
      sender.sendMessage(ChatColor.YELLOW + "For " + formatName + ":");
      sender.sendMessage(format.currentScore().condensed());

      Optional<TournamentFormat> parentOptional =
          Tournament.get().getTournamentManager().currentTournament();
      if (parentOptional.isPresent()) {
        sender.sendMessage(ChatColor.YELLOW + "Overall score (excluding " + formatName + "):");
        sender.sendMessage(parentOptional.get().currentScore().condensed());
      }
    } else {
      sender.sendMessage(format.currentScore().condensed());
    }
  }

  @Subcommand("rounds")
  @Description("Shows the rounds from this event")
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

  @Subcommand("create")
  @Description("Creates a tournament")
  @Syntax("<format>")
  @CommandPermission(STAFF_PERM)
  public void tourney(CommandSender sender, Match match, String pool) {
    manager.createTournament(match, MapFormatXMLParser.parse(pool));
    sender.sendMessage(ChatColor.GOLD + "Starting tournament.");
  }

  @Subcommand("register")
  @Description("Register a team")
  @Syntax("<team>")
  @CommandPermission(STAFF_PERM)
  public void register(CommandSender sender, String name) {
    TournamentTeam team = ConfigTeamParser.getInstance().getTeam(name);
    if (team == null) { // TODO move to provider
      sender.sendMessage(ChatColor.RED + "Team not found!");
      return;
    }

    MatchManager matchManager = PGM.get().getMatchManager();

    for (TournamentPlayer player : team.getPlayers()) {
      Player bukkit = Bukkit.getPlayer(player.getUUID());
      MatchPlayer mp = matchManager.getPlayer(bukkit);
      if (Integration.isVanished(bukkit)) Integration.setVanished(mp, false, false);
    }

    teamManager.addTeam(team);
    sender.sendMessage(ChatColor.YELLOW + "Added team " + team.getName() + "!");
  }

  @Subcommand("list")
  @Description("List all loaded teams")
  @CommandPermission(STAFF_PERM)
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

  @Subcommand("info")
  @Description("View information about a team")
  @Syntax("<team>")
  @CommandPermission(STAFF_PERM)
  public void info(CommandSender sender, String name) {
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

  @Subcommand("unregisterall")
  @Description("Clear all registered teams")
  @CommandPermission(STAFF_PERM)
  public void clear(CommandSender sender) {
    teamManager.clear();
    sender.sendMessage(ChatColor.YELLOW + "Unregistered all teams!");
  }
}
