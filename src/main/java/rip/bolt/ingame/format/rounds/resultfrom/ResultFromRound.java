package rip.bolt.ingame.format.rounds.resultfrom;

import java.util.Map;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.AbstractRound;
import rip.bolt.ingame.format.rounds.RoundDescription;
import rip.bolt.ingame.format.rounds.RoundPhase;
import rip.bolt.ingame.team.TournamentTeam;
import tc.oc.pgm.api.match.Match;

public class ResultFromRound extends AbstractRound<ResultFromSettings> {

    private TournamentFormat format;

    public ResultFromRound(TournamentFormat format, ResultFromSettings settings) {
        super(format, settings);
        this.format = format;
    }

    @Override
    public RoundDescription describe() {
        return new ResultFromDescription(this);
    }

    @Override
    public void start(Match match) {
        setPhase(RoundPhase.FINISHED);
        tournament().nextRound(match);
    }

    @Override
    public Map<TournamentTeam, Integer> scores() {
        return format.references().needRound(settings().targetID()).scores();
    }

}
