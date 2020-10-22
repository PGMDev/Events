package dev.pgm.events.format.rounds;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.team.TournamentTeam;
import java.util.HashMap;
import java.util.Map;
import tc.oc.pgm.api.match.Match;

public abstract class AbstractRound<T extends RoundSettings> implements TournamentRound {

  private final T settings;
  private final Tournament tournament;
  private RoundPhase roundPhase = RoundPhase.UNLOADED;

  public AbstractRound(Tournament tournament, T settings) {
    this.tournament = tournament;
    this.settings = settings;
  }

  @Override
  public String id() {
    return settings.id();
  }

  public Tournament tournament() {
    return tournament;
  }

  @Override
  public RoundPhase phase() {
    return roundPhase;
  }

  @Override
  public void load() {}

  @Override
  public void cleanup(Match match) {}

  @Override
  public TournamentRound currentRound() {
    return this;
  }

  @Override
  public Map<TournamentTeam, Integer> scores() {
    return new HashMap<>();
  }

  @Override
  public boolean shouldShowInHistory() {
    return settings.showInHistory();
  }

  public void setPhase(RoundPhase phase) {
    this.roundPhase = phase;
  }

  @Override
  public T settings() {
    return settings;
  }
}
