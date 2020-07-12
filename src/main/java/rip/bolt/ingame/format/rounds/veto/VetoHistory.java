package rip.bolt.ingame.format.rounds.veto;

import javax.annotation.Nullable;

import rip.bolt.ingame.format.rounds.veto.settings.VetoOption;
import rip.bolt.ingame.format.rounds.veto.settings.VetoSettings;
import rip.bolt.ingame.team.TournamentTeam;

public class VetoHistory {

    private final TournamentTeam team;
    private final VetoSettings.VetoType vetoType;
    private final VetoOption chosen;

    private final boolean shouldAnnounce;

    public VetoHistory(@Nullable TournamentTeam team, VetoSettings.VetoType vetoType, VetoOption chosen, boolean shouldAnnounce) {
        this.team = team;
        this.vetoType = vetoType;
        this.chosen = chosen;
        this.shouldAnnounce = shouldAnnounce;
    }

    public @Nullable TournamentTeam team() {
        return team;
    }

    public VetoSettings.VetoType vetoType() {
        return vetoType;
    }

    public VetoOption optionChosen() {
        return chosen;
    }

    public boolean shouldAnnounce() {
        return shouldAnnounce;
    }
}
