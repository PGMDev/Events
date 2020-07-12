package rip.bolt.ingame.format.rounds.format;

import java.time.Duration;
import java.util.Optional;

import rip.bolt.ingame.format.RoundReferenceHolder;
import rip.bolt.ingame.format.TournamentFormatImpl;
import rip.bolt.ingame.format.TournamentRoundOptions;
import rip.bolt.ingame.format.winner.BestOfCalculation;
import rip.bolt.ingame.team.TournamentTeam;
import rip.bolt.ingame.team.TournamentTeamManager;
import tc.oc.pgm.api.match.Match;

public class FormatTournamentImpl extends TournamentFormatImpl {

    private FormatRound formatRound;

    public FormatTournamentImpl(TournamentTeamManager teamManager, RoundReferenceHolder references, FormatRound formatRound) {
        super(teamManager, new TournamentRoundOptions(false, false, false, Duration.ofSeconds(20), Duration.ofSeconds(30), Duration.ofSeconds(40), new BestOfCalculation<>(formatRound.settings().bestOf())), references);
        this.formatRound = formatRound;
    }

    @Override
    public void onEnd(Match match, Optional<TournamentTeam> winner) {
        formatRound.setWinner(match, winner.isPresent() ? winner.get() : null);
    }

    public FormatRound getFormatRound() {
        return formatRound;
    }

}