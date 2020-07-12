package rip.bolt.ingame.format.rounds.reference;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.AbstractRound;
import rip.bolt.ingame.format.rounds.RoundPhase;
import rip.bolt.ingame.format.rounds.TournamentRound;
import tc.oc.pgm.api.match.Match;

public class ReferenceRound extends AbstractRound<ReferenceRoundSettings> {

    private final TournamentFormat tournamentFormat;

    public ReferenceRound(TournamentFormat format, ReferenceRoundSettings settings) {
        super(format, settings);
        this.tournamentFormat = format;
    }

    @Override
    public ReferenceDescription describe() {
        return new ReferenceDescription(this);
    }

    @Override
    public void start(Match match) {
        setPhase(RoundPhase.RUNNING);
        TournamentRound round = tournamentFormat.references().needRound(settings().targetID());
        //add a copy after this round
        tournamentFormat.addRoundAfterCurrent(round.settings(), settings().futureID());
        //move to the next round (the one we just added)
        setPhase(RoundPhase.FINISHED);
        tournamentFormat.nextRound(match);
    }
}
