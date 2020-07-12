package rip.bolt.ingame.format;

import tc.oc.pgm.api.match.Match;

import java.util.*;
import java.util.stream.Collectors;

import rip.bolt.ingame.context.Context;
import rip.bolt.ingame.format.rounds.RoundDescription;
import rip.bolt.ingame.format.rounds.TournamentRound;
import rip.bolt.ingame.format.score.Scores;
import rip.bolt.ingame.format.winner.TournamentWinnerCalculation;
import rip.bolt.ingame.team.TournamentTeam;
import rip.bolt.ingame.team.TournamentTeamManager;

class RoundHolder {

    private final TournamentWinnerCalculation<TournamentTeam> winnerCalculation;

    private final List<TournamentRound> rounds = new ArrayList<>();
    private final RoundReferenceHolder references;
    private int currentRound = -1;

    public RoundHolder(RoundReferenceHolder references, TournamentWinnerCalculation<TournamentTeam> winnerCalculation) {
        this.references = references;
        this.winnerCalculation = winnerCalculation;
    }

    public RoundReferenceHolder references() {
        return references;
    }

    boolean hasNextRound() {
        return rounds.size() > currentRound + 1;
    }

    public TournamentRound nextRound(Match match) {
        //todo: Check phase of previous round?
        if (currentRound >= 0) {
            rounds.get(currentRound).cleanup(match);
        }
        currentRound++;
        TournamentRound round = rounds.get(currentRound);
        return round;
    }

    public TournamentRound currentRound() {
        return rounds.get(currentRound);
    }

    public List<RoundDescription> roundDescriptions(Context context) {
        return rounds.stream()
                .filter(x -> x.shouldShowInHistory() || context == Context.DEBUG)
                .map(TournamentRound::describe)
                .collect(Collectors.toList());
    }

    public Scores scores(TournamentTeamManager teamManager) {
        ScoreResult scoreResult = scoreResults(teamManager);
        return Scores.ofLegacy(scoreResult.scoreMap, currentRound().scores().keySet());
    }

    public Optional<TournamentTeam> calculateWinner(TournamentTeamManager teamManager) {
        ScoreResult scoreResult = scoreResults(teamManager);
        return winnerCalculation.winningTeam(scoreResult.scoringRounds, scoreResult.scoreMap);
        //if present there's a winner
    }

    private ScoreResult scoreResults(TournamentTeamManager teamManager) {
        Map<TournamentTeam, Integer> scoreMap = new HashMap<>();
        int scoringRounds = 0;
        for (int i = 0; i <= currentRound; i++) {
            TournamentRound round = rounds.get(i);
            if (!round.isScoring()) {
                continue;
            }
            scoringRounds++;
            for (Map.Entry<TournamentTeam, Integer> entry : round.scores().entrySet()) {
                scoreMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        for (TournamentTeam tournamentTeam : teamManager.teams()) {
            //merge in other tournament teams that haven't yet scored
            scoreMap.merge(tournamentTeam, 0, Integer::sum);
        }
        return new ScoreResult(scoreMap, scoringRounds);
    }

    public void addRound(TournamentRound round) {
        references.registerRound(round.id(), round);
        rounds.add(round);
    }

    public void addRoundAfterCurrent(TournamentRound round) {
        references.registerRound(round.id(), round);
        rounds.add(currentRound + 1, round);
    }

    private static class ScoreResult {
        private final Map<TournamentTeam, Integer> scoreMap;
        private final int scoringRounds;

        public ScoreResult(Map<TournamentTeam, Integer> scoreMap, int scoringRounds) {
            this.scoreMap = scoreMap;
            this.scoringRounds = scoringRounds;
        }
    }
}
