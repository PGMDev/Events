package rip.bolt.ingame.format.rounds.replay;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.AbstractRound;
import rip.bolt.ingame.format.rounds.RoundPhase;
import rip.bolt.ingame.format.rounds.TournamentRound;
import rip.bolt.ingame.team.TournamentTeam;
import tc.oc.pgm.api.match.Match;

public class ReplayRound extends AbstractRound<ReplaySettings> {

    private final ReplayDescription replayDescription;
    private boolean isReplaying = false;

    public ReplayRound(TournamentFormat tournamentFormat, ReplaySettings settings) {
        super(tournamentFormat, settings);
        this.replayDescription = new ReplayDescription(this);
    }

    @Override
    public ReplayDescription describe() {
        return replayDescription;
    }

    @Override
    public void start(Match match) {
        setPhase(RoundPhase.RUNNING);
        List<TournamentRound> rounds = settings().referenceSettings()
                .stream()
                .map(x -> tournament().references().round(x.targetID()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(this::tiedRound)
                .collect(Collectors.toList());

        if (rounds.size() == 1) {
            //1 round has been tied, therefore play it
            isReplaying = true;
            TournamentRound toReplay = rounds.get(0);
            tournament().addRoundAfterCurrent(toReplay.settings(), settings().futureID());
        }

        setPhase(RoundPhase.FINISHED);
        tournament().nextRound(match);
    }

    @Override
    public boolean shouldShowInHistory() {
        return !isReplaying;
    }

    private boolean tiedRound(TournamentRound round) {
        int highestScore = Integer.MIN_VALUE;
        boolean tied = true;
        for (Map.Entry<TournamentTeam, Integer> score: round.scores().entrySet()) {
            int teamScore = score.getValue();
            if (teamScore > highestScore) {
                highestScore = teamScore;
                tied = false;
            } else if(teamScore == highestScore) {
                tied = true;
            }
        }

        return tied;
    }

}
