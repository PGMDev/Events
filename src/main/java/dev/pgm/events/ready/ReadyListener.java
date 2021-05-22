package dev.pgm.events.ready;

import static dev.pgm.events.utils.Components.command;
import static net.kyori.adventure.text.Component.text;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.config.AppData;
import java.util.Optional;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.CountdownStartEvent;
import tc.oc.pgm.events.PlayerLeaveMatchEvent;
import tc.oc.pgm.events.PlayerPartyChangeEvent;
import tc.oc.pgm.match.ObserverParty;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.teams.Team;

public class ReadyListener implements Listener {

  private final ReadyManager manager;

  public ReadyListener(ReadyManager manager) {
    this.manager = manager;
  }

  @EventHandler
  public void onQueueStart(CountdownStartEvent event) {
    if (event.getCountdown() instanceof StartCountdown) manager.handleCountdownStart(event);
  }

  @EventHandler
  public void onStart(MatchLoadEvent event) {
    manager.reset();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onLeave(PlayerLeaveMatchEvent event) {
    if (!AppData.autoUnready()) {
      return;
    }

    Party party = event.getParty();
    if (party instanceof ObserverParty) {
      return;
    }

    // if match starting and team was ready unready them
    if (event.getMatch().getPhase() == MatchPhase.STARTING && manager.isReady(party)) {
      manager.unready(party, null);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPartyChange(PlayerPartyChangeEvent event) {
    if (!AppData.readyReminders()) {
      return;
    }

    MatchPlayer player = event.getPlayer();
    Optional<Team> playerTeam = EventsPlugin.get().getTeamManager().playerTeam(player.getId());

    // Add hint to ready up once all players joined
    if (playerTeam.isPresent()
        && manager.canReady(event.getMatch()).isAllowed()
        && manager.canReady(playerTeam.get()).isAllowed()) {

      TextComponent readyHint =
          text("Mark your team as ready using ", NamedTextColor.GREEN)
              .append(
                  command(Style.style(NamedTextColor.YELLOW, TextDecoration.UNDERLINED), "ready"));

      Bukkit.getScheduler()
          .scheduleSyncDelayedTask(
              EventsPlugin.get(),
              () -> {
                // Delay message sending to ensure after motd message
                playerTeam.get().sendMessage(readyHint);
              },
              20);
    }
  }
}
