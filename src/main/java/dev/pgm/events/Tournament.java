package dev.pgm.events;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.InvalidCommandArgument;
import dev.pgm.events.commands.ReadyCommands;
import dev.pgm.events.commands.TournamentCommands;
import dev.pgm.events.commands.VetoCommands;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.format.FormatRound;
import dev.pgm.events.listeners.MatchLoadListener;
import dev.pgm.events.listeners.PlayerJoinListen;
import dev.pgm.events.ready.ReadyListener;
import dev.pgm.events.ready.ReadyManager;
import dev.pgm.events.ready.ReadyManagerImpl;
import dev.pgm.events.ready.ReadyParties;
import dev.pgm.events.ready.ReadySystem;
import dev.pgm.events.team.ConfigTeamParser;
import dev.pgm.events.team.DefaultTeamManager;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;

public class Tournament extends JavaPlugin {

  private BukkitCommandManager commands;
  private TournamentTeamManager teamManager;
  private TournamentManager tournamentManager;
  private ReadyManager readyManager;

  private static Tournament plugin;

  @Override
  public void onEnable() {
    plugin = this;
    saveDefaultConfig();

    commands = new BukkitCommandManager(this);
    teamManager = DefaultTeamManager.manager();
    tournamentManager = new TournamentManager();
    ConfigTeamParser.getInstance(); // load teams now

    readyManager = new ReadyManagerImpl(new ReadySystem(), new ReadyParties());
    ReadyListener readyListener = new ReadyListener(readyManager);

    Bukkit.getPluginManager().registerEvents(new MatchLoadListener(teamManager), this);
    Bukkit.getPluginManager().registerEvents(new PlayerJoinListen(teamManager), this);
    Bukkit.getPluginManager().registerEvents(readyListener, this);

    registerCommands();
  }

  @Override
  public void onDisable() {
    plugin = null;
  }

  public TournamentTeamManager getTeamManager() {
    return teamManager;
  }

  public TournamentManager getTournamentManager() {
    return tournamentManager;
  }

  public static Tournament get() {
    return plugin;
  }

  private void registerCommands() {
    commands.registerDependency(TournamentManager.class, tournamentManager);
    commands.registerDependency(TournamentTeamManager.class, teamManager);
    commands.registerDependency(ReadyManager.class, readyManager);

    commands
        .getCommandContexts()
        .registerIssuerOnlyContext(
            Match.class, c -> PGM.get().getMatchManager().getMatch(c.getSender()));

    commands
        .getCommandContexts()
        .registerIssuerOnlyContext(
            MatchPlayer.class,
            c -> {
              if (!c.getIssuer().isPlayer()) {
                throw new InvalidCommandArgument("You are unable to run this command", false);
              }
              final MatchPlayer player = PGM.get().getMatchManager().getPlayer(c.getPlayer());
              if (player != null) {
                return player;
              }
              throw new InvalidCommandArgument(
                  "Sorry, an error occured while resolving your player", false);
            });

    commands
        .getCommandContexts()
        .registerIssuerOnlyContext(
            TournamentFormat.class,
            c -> {
              Optional<TournamentFormat> tournamentFormat = tournamentManager.currentTournament();
              if (tournamentFormat.isPresent()) {
                TournamentFormat format = tournamentFormat.get();
                if (format.currentRound() == null) return format;

                if (format.currentRound() instanceof FormatRound)
                  format = ((FormatRound) format.currentRound()).formatTournament();

                if (format == null)
                  format = tournamentFormat.get(); // FormatTournamentImpl = null after round ends

                return format;
              }
              throw new InvalidCommandArgument("No tournament is currently running!", false);
            });

    commands.registerCommand(new VetoCommands());
    commands.registerCommand(new ReadyCommands());
    commands.registerCommand(new TournamentCommands());
  }
}
