package rip.bolt.ingame.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import org.jvnet.hk2.annotations.Optional;
import rip.bolt.ingame.Tournament;
import rip.bolt.ingame.TournamentManager;
import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.team.ConfigTeamParser;
import rip.bolt.ingame.team.TournamentPlayer;
import rip.bolt.ingame.team.TournamentTeam;
import rip.bolt.ingame.team.TournamentTeamManager;
import rip.bolt.ingame.utils.UUIDFetcher;
import rip.bolt.ingame.xml.MapFormatXMLParser;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.lib.app.ashcon.intake.parametric.annotation.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class TournamentAdminCommands {

    @Command(aliases = "create", desc = "Creates a tournament", usage = "<format>", perms = "ingame.staff")
    public void tourney(CommandSender sender, TournamentManager manager, Match match, @Nullable String pool) {
        if(pool == null){
            sender.sendMessage(ChatColor.GOLD + "------- " + ChatColor.AQUA + "Loaded formats" + ChatColor.GOLD + " -------");
            for (String format : MapFormatXMLParser.parseAll())
                sender.sendMessage(ChatColor.AQUA + "- " + format);
            return;
        }

        TournamentFormat tournamentFormat = MapFormatXMLParser.parse(pool);
        if(tournamentFormat == null){
            sender.sendMessage(ChatColor.RED + "Tournament format not found!");
            return;
        }

        manager.createTournament(match, tournamentFormat);
        sender.sendMessage(ChatColor.GOLD + "Starting tournament.");
    }

    @Command(aliases = "register", desc = "Register a team", usage = "<team>", perms = "ingame.staff")
    public void register(CommandSender sender, TournamentTeamManager teamManager, @Text String name) {
        TournamentTeam team = ConfigTeamParser.getInstance().getTeam(name);
        if (team == null) { // TODO move to provider
            sender.sendMessage(ChatColor.RED + "Team not found!");
            return;
        }

        teamManager.addTeam(team);
        sender.sendMessage(ChatColor.YELLOW + "Added team " + team.getName() + "!");
    }

    @Command(aliases = "add", desc = "Add a player to a team", usage = "<player> <team>", perms = "ingame.staff")
    public void add(CommandSender sender, TournamentTeamManager teamManager, String userName, @Text String teamName) {
        TournamentTeam team = ConfigTeamParser.getInstance().getTeam(teamName);

        if (team == null) { // TODO move to provider
            sender.sendMessage(ChatColor.RED + "Team not found!");
            return;
        }

        UUIDFetcher.getUUID(userName, uuid -> {
            if(uuid == null){
                sender.sendMessage(ChatColor.RED + "No player with name: '" + userName + "' found!");
                return;
            }
            //need to prevent a player being part of multiple teams
            teamManager.removePlayerFromTeams(uuid);

            team.addPlayer(TournamentPlayer.create(uuid, true));
            sender.sendMessage(ChatColor.YELLOW + "Added player: " + userName + " to team: " + teamName + "!");
        });
    }

    @Command(aliases = "remove", desc = "Remove a player from a team", usage = "<player> <team>", perms = "ingame.staff")
    public void remove(CommandSender sender, String userName, @Text String teamName) {
        TournamentTeam team = ConfigTeamParser.getInstance().getTeam(teamName);

        if (team == null) { // TODO move to provider
            sender.sendMessage(ChatColor.RED + "Team not found!");
            return;
        }

        UUIDFetcher.getUUID(userName, uuid -> {
            if(uuid == null){
                sender.sendMessage(ChatColor.RED + "No player with name: '" + userName + "' found!");
                return;
            }

            team.removePlayer(uuid);
            sender.sendMessage(ChatColor.YELLOW + "Removed player: " + userName + " from team: " + teamName + "!");
        });
    }

    @Command(aliases = "list", desc = "List all loaded teams", perms = "ingame.staff")
    public void list(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "------- " + ChatColor.AQUA + "Registered Teams" + ChatColor.GOLD + " -------");
        for (TournamentTeam team : ConfigTeamParser.getInstance().getTeams())
            sender.sendMessage(ChatColor.AQUA + "- " + team.getName());
        sender.sendMessage(ChatColor.YELLOW + "Run /tourney info <team> to see player roster!");
    }

    @Command(aliases = "reload", desc = "Reload all teams", perms = "ingame.staff")
    public void reload(CommandSender sender) {
        int numberOfTeams = ConfigTeamParser.getInstance().reloadTeams();

        sender.sendMessage(ChatColor.YELLOW + "Loaded " + numberOfTeams + " teams!");
        sender.sendMessage(ChatColor.YELLOW + "Run /tourney list to see all teams!");
    }

    @Command(aliases = "info", desc = "View information about a team", usage = "<team>", perms = "ingame.staff")
    public void info(CommandSender sender, @Text String name) {
        TournamentTeam team = ConfigTeamParser.getInstance().getTeam(name);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Team not found!");
            return;
        }

        sender.sendMessage(ChatColor.GOLD + "------- " + ChatColor.AQUA + team.getName() + ChatColor.GOLD + " -------");
        for (TournamentPlayer player : team.getPlayers()) {
            String playerName = player.getUUID().toString() + ChatColor.GRAY + " (player hasn't logged on)";
            OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUUID());
            if (offline.getName() != null)
                playerName = offline.getName();

            sender.sendMessage(ChatColor.AQUA + "- " + playerName);
        }
    }

    @Command(aliases = "unregisterall", desc = "Clear all registered teams", perms = "ingame.staff")
    public void clear(CommandSender sender, TournamentTeamManager teamManager) {
        teamManager.clear();
        sender.sendMessage(ChatColor.YELLOW + "Unregistered all teams!");
    }

}
