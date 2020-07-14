package rip.bolt.ingame;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import rip.bolt.ingame.api.APIManager;
import rip.bolt.ingame.commands.RankedAdminCommands;
import rip.bolt.ingame.commands.TournamentAdminCommands;
import rip.bolt.ingame.commands.TournamentUserCommands;
import rip.bolt.ingame.commands.providers.TournamentProvider;
import rip.bolt.ingame.config.AppData;
import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.listeners.MatchLoadListener;
import rip.bolt.ingame.listeners.PlayerJoinListen;
import rip.bolt.ingame.ready.ReadyCommands;
import rip.bolt.ingame.ready.ReadyListener;
import rip.bolt.ingame.ready.ReadyParties;
import rip.bolt.ingame.ready.ReadySystem;
import rip.bolt.ingame.team.ConfigTeamParser;
import rip.bolt.ingame.team.DefaultTeamManager;
import rip.bolt.ingame.team.TournamentTeamManager;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.command.graph.CommandExecutor;
import tc.oc.pgm.command.graph.MatchPlayerProvider;
import tc.oc.pgm.command.graph.MatchProvider;
import tc.oc.pgm.lib.app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import tc.oc.pgm.lib.app.ashcon.intake.fluent.DispatcherNode;
import tc.oc.pgm.lib.app.ashcon.intake.parametric.AbstractModule;

public class Tournament extends JavaPlugin {

    private TournamentTeamManager teamManager;
    private TournamentManager tournamentManager;

    private RankedManager rankedManager;
    private APIManager apiManager;

    private static Tournament plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        teamManager = DefaultTeamManager.manager();
        tournamentManager = new TournamentManager();

        if (AppData.API.isEnabled()) {
            apiManager = new APIManager(AppData.API.getURL(), AppData.API.getGetMatchPath(), AppData.API.getMatchResultsPath());
            rankedManager = new RankedManager();
            Bukkit.getPluginManager().registerEvents(rankedManager, this);
        } else {
            ConfigTeamParser.getInstance(); // load teams now
        }

        ReadySystem system = new ReadySystem();
        ReadyParties parties = new ReadyParties();
        ReadyListener readyListener = new ReadyListener(system, parties);
        ReadyCommands readyCommands = new ReadyCommands(system, parties);

        BasicBukkitCommandGraph g = new BasicBukkitCommandGraph(new CommandModule(tournamentManager, teamManager));
        DispatcherNode node = g.getRootDispatcherNode();
        node.registerCommands(new TournamentUserCommands());
        node.registerCommands(readyCommands);
        node.registerCommands(new RankedAdminCommands());
        node = node.registerNode("tourney", "tournament", "tm");
        node.registerCommands(new TournamentAdminCommands());

        Bukkit.getPluginManager().registerEvents(new MatchLoadListener(teamManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListen(teamManager), this);
        Bukkit.getPluginManager().registerEvents(readyListener, this);
        new CommandExecutor(this, g).register();

        System.out.println("[Ingame] Ingame is now enabled!");
    }

    @Override
    public void onDisable() {
        plugin = null;
        System.out.println("[Ingame] Ingame is now disabled!");
    }

    public APIManager getApiManager() {
        return apiManager;
    }

    public RankedManager getRankedManager() {
        return rankedManager;
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

    private static class CommandModule extends AbstractModule {

        private final TournamentManager tournamentManager;
        private final TournamentTeamManager teamManager;

        public CommandModule(TournamentManager tournamentManager, TournamentTeamManager teamManager) {
            this.tournamentManager = tournamentManager;
            this.teamManager = teamManager;
        }

        @Override
        protected void configure() {
            configureInstances();
            configureProviders();
        }

        private void configureInstances() {
            bind(PGM.class).toInstance(PGM.get());
        }

        private void configureProviders() {
            bind(MatchPlayer.class).toProvider(new MatchPlayerProvider());
            bind(Match.class).toProvider(new MatchProvider());
            bind(TournamentManager.class).toInstance(tournamentManager);
            bind(TournamentTeamManager.class).toInstance(teamManager);
            bind(TournamentFormat.class).toProvider(new TournamentProvider(tournamentManager));
        }

    }

}
