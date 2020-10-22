package dev.pgm.events.format;

import dev.pgm.events.config.Context;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import dev.pgm.events.format.score.FormattedScore;
import dev.pgm.events.format.score.Scores;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.List;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.Match;

/** A set of {@link TournamentRound}s and/or {@link Tournament}s */
public interface Tournament {

  /**
   * Adds a {@link Listener} that will persist during the given scope
   *
   * @see TournamentScope
   * @param listener the listener to register
   * @param scope the scope to register the listener for
   */
  void addListener(Listener listener, TournamentScope scope);

  /** Unregisters all previously registered listeners */
  void unregisterAll();

  default void suspend() {
    unregisterAll();
  }

  /**
   * Starts the next round. If there is a winner after the last match finish the tournament //TODO:
   * Move the winner check a level up
   *
   * @param match the match to start the next round in
   */
  void nextRound(Match match);

  /** @return if there is any rounds after the current one */
  boolean hasNextRound();

  /**
   * @see TournamentState
   * @return the current state of this tournament
   */
  TournamentState state();

  /** @return the current formatted scores of this tournament */
  FormattedScore currentScore();

  default List<RoundDescription> roundsInformation() {
    return roundsInformation(Context.NORMAL);
  }

  /**
   * Gets descriptions of all currently registered rounds
   *
   * <p>{@link Context#DEBUG} will override {@link TournamentRound#shouldShowInHistory()}
   *
   * @param context the context to view this
   * @return a list of descriptions for all currently registered rounds
   */
  List<RoundDescription> roundsInformation(Context context);

  /** @return the current scores */
  Scores scores();

  /** @return the currently active round */
  TournamentRound currentRound();

  /** Adds a round to this tournament */
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

  /** Adds a round after the current round to this tournament */
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
