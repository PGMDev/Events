package dev.pgm.events.format.rounds.veto;

import dev.pgm.events.format.RoundReferenceHolder;
import dev.pgm.events.format.TournamentFormatImpl;
import dev.pgm.events.format.TournamentRoundOptions;
import dev.pgm.events.format.score.Score;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import tc.oc.pgm.api.match.Match;

public class VetoTournamentImpl extends TournamentFormatImpl {

  private final VetoRound vetoRound;

  public VetoTournamentImpl(
      TournamentTeamManager teamManager,
      TournamentRoundOptions options,
      VetoRound vetoRound,
      RoundReferenceHolder referenceHolder) {
    super(teamManager, options, referenceHolder);
    this.vetoRound = vetoRound;
  }

  @Override
  public void onEnd(Match match, Optional<TournamentTeam> winner) {
    // list in descending order of score
    List<TournamentTeam> vetoOrder =
        this.scores().scores().stream()
            .sorted(Comparator.comparingInt(Score::score).reversed())
            .map(Score::team)
            .collect(Collectors.toList());

    // unregister everything with this fake event
    unregisterAll();

    // call callback to start the veto process
    vetoRound.startVeto(match, vetoOrder);
  }
}
