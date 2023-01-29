package dev.pgm.events;

import dev.pgm.events.api.teams.ConfigTeams;
import dev.pgm.events.api.teams.DefaultTeamRegistry;
import dev.pgm.events.api.teams.TournamentTeamRegistry;
import dev.pgm.events.commands.EventsCommandGraph;
import dev.pgm.events.listeners.MatchLoadListener;
import dev.pgm.events.listeners.PlayerJoinListen;
import dev.pgm.events.ready.ReadyCommands;
import dev.pgm.events.ready.ReadyListener;
import dev.pgm.events.ready.ReadyManager;
import dev.pgm.events.ready.ReadyManagerImpl;
import dev.pgm.events.ready.ReadyParties;
import dev.pgm.events.ready.ReadySystem;
import dev.pgm.events.team.DefaultTeamManager;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EventsPlugin extends JavaPlugin {

  private TournamentTeamManager teamManager;
  private TournamentManager tournamentManager;
  private TournamentTeamRegistry teamRegistry;

  private static EventsPlugin plugin;

  @Override
  public void onEnable() {
    plugin = this;
    saveDefaultConfig();

    teamManager = DefaultTeamManager.manager();
    tournamentManager = new TournamentManager();
    teamRegistry = DefaultTeamRegistry.createRegistry(new ConfigTeams());

    ReadyManager readyManager = new ReadyManagerImpl(new ReadySystem(), new ReadyParties());
    ReadyListener readyListener = new ReadyListener(readyManager);
    ReadyCommands readyCommands = new ReadyCommands(readyManager);

    EventsCommandGraph eventsCommandGraph;
    try {
      eventsCommandGraph = new EventsCommandGraph(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    eventsCommandGraph.registerCommands(Collections.singletonList(readyCommands));

    Bukkit.getPluginManager().registerEvents(new MatchLoadListener(teamManager), this);
    Bukkit.getPluginManager().registerEvents(new PlayerJoinListen(teamManager), this);
    Bukkit.getPluginManager().registerEvents(readyListener, this);
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

  public TournamentTeamRegistry getTeamRegistry() {
    return teamRegistry;
  }

  public void setTeamRegistry(TournamentTeamRegistry teamRegistry) {
    this.teamRegistry = teamRegistry;
  }

  public static EventsPlugin get() {
    return plugin;
  }
}
