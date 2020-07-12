package rip.bolt.ingame.format.events;

import org.bukkit.event.Event;

import rip.bolt.ingame.format.TournamentFormat;

public class TournamentEvent extends Event {

    private final TournamentFormat tournamentFormat;

    public TournamentEvent(TournamentFormat tournamentFormat) {
        this.tournamentFormat = tournamentFormat;
    }

    public TournamentFormat format() {
        return tournamentFormat;
    }
}
