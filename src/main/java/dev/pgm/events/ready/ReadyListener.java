package dev.pgm.events.ready;

import java.time.Duration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.events.CountdownCancelEvent;
import tc.oc.pgm.events.CountdownStartEvent;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.start.StartMatchModule;

public class ReadyListener implements Listener {

  private final ReadySystem system;
  private final ReadyParties parties;

  public ReadyListener(ReadySystem system, ReadyParties parties) {
    this.system = system;
    this.parties = parties;
  }

  @EventHandler
  public void onQueueStart(CountdownStartEvent event) {
    if (event.getCountdown() instanceof StartCountdown)
      system.onStart(
          ((StartCountdown) event.getCountdown()).getRemaining(),
          parties.allReady(event.getMatch()));
  }

  @EventHandler
  public void onCancel(CountdownCancelEvent event) {
    if (!(event.getCountdown() instanceof StartCountdown)) return;

    Duration remaining = system.onCancel(parties.allReady(event.getMatch()));
    if (remaining != null)
      event
          .getMatch()
          .needModule(StartMatchModule.class)
          .forceStartCountdown(remaining, Duration.ZERO);
  }

  @EventHandler
  public void onStart(MatchLoadEvent event) {
    system.reset();
  }
}
