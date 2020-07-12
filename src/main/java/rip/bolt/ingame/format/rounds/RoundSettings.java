package rip.bolt.ingame.format.rounds;

import rip.bolt.ingame.format.TournamentFormat;

public abstract class RoundSettings {

    private final String id;
    private final boolean scoring;
    private final boolean showInHistory;

    public RoundSettings(String id, boolean scoring, boolean showInHistory) {
        this.id = id;
        this.scoring = scoring;
        this.showInHistory = showInHistory;
    }

    public String id() {
        return id;
    }

    public boolean scoring() {
        return scoring;
    }

    public boolean showInHistory() {
        return showInHistory;
    }

    public abstract TournamentRound newRound(TournamentFormat format);

    /**
     * Use this for duplication of rounds, round specific information is not copied over (such as the
     * round id) -- but everything else is. Differs from new round in
     *
     * @param format the tournament format
     * @param id the id of the new round to be created
     * @return
     */
    public abstract TournamentRound newRound(TournamentFormat format, String id);
}
