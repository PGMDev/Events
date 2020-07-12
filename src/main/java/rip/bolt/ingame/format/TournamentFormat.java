package rip.bolt.ingame.format;

import org.bukkit.event.Listener;

import rip.bolt.ingame.context.Context;
import rip.bolt.ingame.format.rounds.RoundDescription;
import rip.bolt.ingame.format.rounds.RoundSettings;
import rip.bolt.ingame.format.rounds.TournamentRound;
import rip.bolt.ingame.format.score.FormattedScore;
import rip.bolt.ingame.format.score.Scores;
import rip.bolt.ingame.team.TournamentTeamManager;
import tc.oc.pgm.api.match.Match;

import java.util.List;

public interface TournamentFormat {

    void addListener(Listener listener, TournamentScope scope);

    void unregisterAll();

    default void suspend() {
        unregisterAll();
    }

    void nextRound(Match match);

    boolean hasNextRound();

    TournamentState state();

    FormattedScore currentScore();

    default List<RoundDescription> roundsInformation() {
        return roundsInformation(Context.NORMAL);
    }

    List<RoundDescription> roundsInformation(Context context);

    Scores scores();

    TournamentRound currentRound();

    void addRound(TournamentRound round);

    default void addRound(List<? extends TournamentRound> rounds) {
        for (int i = rounds.size() - 1; i >= 0; i--) {
            addRound(rounds.get(i));
        }
    }

    default void addRound(RoundSettings roundSettings) {
        addRound(roundSettings.newRound(this));
    }

    default void addRound(RoundSettings roundSettings, String id) {
        addRound(roundSettings.newRound(this, id));
    }

    void addRoundAfterCurrent(TournamentRound round);

    default void addRoundAfterCurrent(List<? extends TournamentRound> rounds) {
        for (int i = rounds.size() - 1; i >= 0; i--) {
            addRoundAfterCurrent(rounds.get(i));
        }
    }

    default void addRoundAfterCurrent(RoundSettings roundSettings) {
        addRoundAfterCurrent(roundSettings.newRound(this));
    }

    default void addRoundAfterCurrent(RoundSettings roundSettings, String id) {
        addRoundAfterCurrent(roundSettings.newRound(this, id));
    }

    TournamentTeamManager teamManager();

    TournamentRoundOptions options();

    RoundReferenceHolder references();
}
