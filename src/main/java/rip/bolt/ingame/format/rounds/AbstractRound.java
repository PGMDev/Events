package rip.bolt.ingame.format.rounds;

import java.util.HashMap;
import java.util.Map;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.team.TournamentTeam;
import tc.oc.pgm.api.match.Match;

public abstract class AbstractRound<T extends RoundSettings> implements TournamentRound {

    private final T settings;
    private final TournamentFormat tournamentFormat;
    private RoundPhase roundPhase = RoundPhase.UNLOADED;

    public AbstractRound(TournamentFormat tournamentFormat, T settings) {
        this.tournamentFormat = tournamentFormat;
        this.settings = settings;
    }

    @Override
    public String id() {
        return settings.id();
    }

    public TournamentFormat tournament() {
        return tournamentFormat;
    }

    @Override
    public RoundPhase phase() {
        return roundPhase;
    }

    @Override
    public void load() {

    }

    @Override
    public void cleanup(Match match) {

    }

    @Override
    public TournamentRound currentRound() {
        return this;
    }

    @Override
    public Map<TournamentTeam, Integer> scores() {
        return new HashMap<>();
    }

    @Override
    public boolean shouldShowInHistory() {
        return settings.showInHistory();
    }

    public void setPhase(RoundPhase phase) {
        this.roundPhase = phase;
    }

    @Override
    public T settings() {
        return settings;
    }
}
