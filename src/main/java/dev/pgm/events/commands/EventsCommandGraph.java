package dev.pgm.events.commands;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.TournamentManager;
import dev.pgm.events.api.teams.TournamentTeamRegistry;
import dev.pgm.events.commands.providers.TournamentProvider;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.command.injectors.MatchPlayerProvider;
import tc.oc.pgm.command.injectors.MatchProvider;
import tc.oc.pgm.command.injectors.PlayerProvider;
import tc.oc.pgm.command.util.CommandGraph;
import tc.oc.pgm.lib.cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import tc.oc.pgm.lib.cloud.commandframework.minecraft.extras.MinecraftHelp;
import tc.oc.pgm.util.Audience;

public class EventsCommandGraph extends CommandGraph<EventsPlugin> {

  public EventsCommandGraph(EventsPlugin plugin) throws Exception {
    super(plugin);
  }

  @Override
  protected MinecraftHelp<CommandSender> createHelp() {
    return new MinecraftHelp<>("/events help", Audience::get, manager);
  }

  @Override
  protected CommandConfirmationManager<CommandSender> createConfirmationManager() {
    return null;
  }

  @Override
  protected void setupInjectors() {
    // PGM Injectors
    registerInjector(PGM.class, PGM::get);
    registerInjector(Match.class, new MatchProvider());
    registerInjector(MatchPlayer.class, new MatchPlayerProvider());
    registerInjector(Player.class, new PlayerProvider());

    // Events Injectors
    registerInjector(TournamentManager.class, EventsPlugin::getTournamentManager);
    registerInjector(TournamentTeamRegistry.class, EventsPlugin::getTeamRegistry);
    registerInjector(TournamentTeamManager.class, EventsPlugin::getTeamManager);
    registerInjector(TournamentFormat.class, new TournamentProvider(plugin.getTournamentManager()));
  }

  @Override
  protected void setupParsers() {
    // No custom parses used
  }

  @Override
  public void registerCommands() {
    register(new VetoCommands());
    register(new TournamentUserCommands());
    register(new TournamentAdminCommands());
  }

  public <T> void registerCommands(List<T> commands) {
    commands.forEach(this::register);
  }
}
