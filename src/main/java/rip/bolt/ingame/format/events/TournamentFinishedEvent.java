package rip.bolt.ingame.format.events;

import org.bukkit.event.HandlerList;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.team.TournamentTeam;

import java.util.Optional;

public class TournamentFinishedEvent extends TournamentEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Optional<TournamentTeam> winningTeam;

    public TournamentFinishedEvent(TournamentFormat tournamentFormat, Optional<TournamentTeam> winningTeam) {
        super(tournamentFormat);
        this.winningTeam = winningTeam;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Optional<TournamentTeam> winningTeam() {
        return winningTeam;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
