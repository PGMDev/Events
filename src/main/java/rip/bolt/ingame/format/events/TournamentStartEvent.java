package rip.bolt.ingame.format.events;

import org.bukkit.event.HandlerList;

import rip.bolt.ingame.format.TournamentFormat;

public class TournamentStartEvent extends TournamentEvent {

    public TournamentStartEvent(TournamentFormat tournamentFormat) {
        super(tournamentFormat);
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
