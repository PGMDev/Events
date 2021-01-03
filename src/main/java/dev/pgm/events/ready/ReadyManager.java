package dev.pgm.events.ready;

import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.teams.Team;

import java.time.Duration;

public interface ReadyManager {

    default void createMatchStart(Match match) {
        createMatchStart(match, Duration.ofSeconds(20));
    }

    void createMatchStart(Match match, Duration duration);

    void cancelMatchStart(Match match);

    void readyTeam(Party party);

    void unreadyTeam(Party party);

    boolean isReady(Party party);

    boolean allReady(Match match);

    boolean unreadyShouldCancel();

    boolean canReadyAction();

    void reset();

    Duration cancelDuration(Match match);

    void onStart(Match match, Duration duration);
}
