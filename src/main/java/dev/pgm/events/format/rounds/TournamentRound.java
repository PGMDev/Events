package dev.pgm.events.format.rounds;

import dev.pgm.events.team.TournamentTeam;
import java.util.Map;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.match.Match;

/** A tournament round, is tied to a single {@link Match} */
// TODO move abstract round stuff into here? Types for setting, currentRound()
public interface TournamentRound extends Listener {

  /** @return the id of this round (e.g "veto-decider") */
  String id();

  /** Contains different info about this round and ways to format them */
  RoundDescription describe();

  /**
   * loads all necessary information and throws any errors tied to this process(e.g loading a
   * specified map)
   */
  void load();

  /** start this round (cycle, enable countdowns etc...) */
  void start(Match match);

  /**
   * finalize the match if its not already over, unregister listeners, add hint for garbage
   * collector on obsolete objects
   */
  void cleanup(Match match);

  /** @return the current phase of this round */
  RoundPhase phase();

  /** @return the scores for this round(1 point is one match win) */
  Map<TournamentTeam, Integer> scores();

  /** @return true if the scores of this count to the overall series, false otherwise */
  default boolean isScoring() {
    return settings().scoring();
  }

  /** @return weather this round should show in play history */
  boolean shouldShowInHistory();

  /**
   * This will mostly return this instance(itself), but can in some cases return another round. The
   * most obvious case would be in a {@link dev.pgm.events.format.rounds.veto.VetoRound}, if the
   * veto decider is currently playing, this will return that round.
   *
   * @return the currently playing round
   */
  TournamentRound currentRound();

  /** @return the settings of this round */
  RoundSettings settings();
}
