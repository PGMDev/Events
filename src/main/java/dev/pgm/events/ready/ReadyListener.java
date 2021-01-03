package dev.pgm.events.ready;

import dev.pgm.events.Tournament;
import dev.pgm.events.config.AppData;
import dev.pgm.events.utils.Parties;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.CountdownCancelEvent;
import tc.oc.pgm.events.CountdownStartEvent;
import tc.oc.pgm.events.PlayerLeaveMatchEvent;
import tc.oc.pgm.events.PlayerPartyChangeEvent;
import tc.oc.pgm.lib.net.kyori.adventure.text.Component;
import tc.oc.pgm.lib.net.kyori.adventure.text.format.NamedTextColor;
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
  public void onCancel(CountdownCancelEvent event) {
    if (event.getCountdown() instanceof StartCountdown) manager.handleCountdownCancel(event);
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
      manager.unready(party);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPartyChange(PlayerPartyChangeEvent event) {
    if (!AppData.readyReminders()) {
      return;
    }

    MatchPlayer player = event.getPlayer();
    Optional<Team> playerTeam = Tournament.get().getTeamManager().playerTeam(player.getId());

    // Add hint to ready up once all players joined
    if (playerTeam.isPresent()
        && !event.getMatch().isRunning()
        && Parties.isFull(player.getParty())) {
      Bukkit.getScheduler()
          .scheduleSyncDelayedTask(
              Tournament.get(),
              () -> {
                // Delay message sending to ensure after motd message
                event
                    .getPlayer()
                    .getParty()
                    .sendMessage(
                        Component.text("Mark your team as ready using ", NamedTextColor.GREEN)
                            .append(Component.text("/ready", NamedTextColor.YELLOW))
                            .append(Component.text(".", NamedTextColor.GREEN)));
              },
              20);
    }
  }
}
