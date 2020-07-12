package rip.bolt.ingame.ready;

import java.time.Duration;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.events.CancelMatchStartCountdownEvent;
import tc.oc.pgm.events.InitiateMatchStartCountdownEvent;
import tc.oc.pgm.start.StartMatchModule;

public class ReadyListener implements Listener {

    private final ReadySystem system;
    private final ReadyParties parties;

    public ReadyListener(ReadySystem system, ReadyParties parties) {
        this.system = system;
        this.parties = parties;
    }

    @EventHandler
    public void onQueueStart(InitiateMatchStartCountdownEvent event) {
        system.onStart(event.duration(), parties.allReady(event.getMatch()));
    }

    @EventHandler
    public void onCancel(CancelMatchStartCountdownEvent event) {
        Duration remaining = system.onCancel(parties.allReady(event.getMatch()));
        if (remaining != null)
            event.getMatch().needModule(StartMatchModule.class).forceStartCountdown(remaining, Duration.ZERO);
    }

    @EventHandler
    public void onStart(MatchLoadEvent event) {
        system.reset();
    }

}
