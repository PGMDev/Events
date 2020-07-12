package rip.bolt.ingame.format.rounds.veto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import rip.bolt.ingame.format.RoundReferenceHolder;
import rip.bolt.ingame.format.TournamentFormatImpl;
import rip.bolt.ingame.format.TournamentRoundOptions;
import rip.bolt.ingame.format.score.Score;
import rip.bolt.ingame.team.TournamentTeam;
import rip.bolt.ingame.team.TournamentTeamManager;
import tc.oc.pgm.api.match.Match;

public class VetoTournamentImpl extends TournamentFormatImpl {

    //private final Consumer<List<TournamentTeam>> func;
    private final VetoRound vetoRound;

    public VetoTournamentImpl(TournamentTeamManager teamManager, TournamentRoundOptions options, VetoRound vetoRound, RoundReferenceHolder referenceHolder) {
        super(teamManager, options, referenceHolder);
        this.vetoRound = vetoRound;
    }

    @Override
    public void onEnd(Match match, Optional<TournamentTeam> winner) {
        //list in descending order of score
        List<TournamentTeam> vetoOrder = this.scores().scores().stream()
                .sorted(Comparator.comparingInt(Score::score).reversed())
                .map(Score::team)
                .collect(Collectors.toList());

        //unregister everything with this fake event
        unregisterAll();

        //call callback to start the veto process
        vetoRound.startVeto(match, vetoOrder);
    }

}
