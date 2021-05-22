package dev.pgm.events.format;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.api.events.TournamentFinishedEvent;
import dev.pgm.events.config.Context;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.TournamentRound;
import dev.pgm.events.format.score.FormattedScore;
import dev.pgm.events.format.score.Scores;
import dev.pgm.events.format.shutdown.ShutdownCountdown;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.util.bukkit.Events;

public class TournamentFormatImpl implements TournamentFormat {

  private final List<Listener> permanentListeners = new ArrayList<>();
  private final List<Listener> roundListeners = new ArrayList<>();

  private final RoundHolder roundHolder;
  private final TournamentTeamManager teamManager;
  private final TournamentRoundOptions tournamentRoundOptions;

  private TournamentState state = TournamentState.WAITING;

  public TournamentFormatImpl(
      TournamentTeamManager teamManager,
      TournamentRoundOptions options,
      RoundReferenceHolder references) {
    this.roundHolder = new RoundHolder(references, options.winnerCalculation());
    this.teamManager = teamManager;
    this.tournamentRoundOptions = options;
  }

  @Override
  public TournamentTeamManager teamManager() {
    return teamManager;
  }

  @Override
  public RoundReferenceHolder references() {
    return roundHolder.references();
  }

  @Override
  public TournamentRoundOptions options() {
    return tournamentRoundOptions;
  }

  @Override
  public void addListener(Listener listener, TournamentScope scope) {
    if (scope == TournamentScope.ROUND) roundListeners.add(listener);
    else permanentListeners.add(listener);

    Bukkit.getPluginManager().registerEvents(listener, PGM.get());
  }

  @Override
  public void unregisterAll() {
    // unregister all listeners
    permanentListeners.forEach(HandlerList::unregisterAll);
    roundListeners.forEach(HandlerList::unregisterAll);

    // clear lists after this
    permanentListeners.clear();
    roundListeners.clear();
  }

  @Override
  public void nextRound(Match match) {
    if (state == TournamentState.WAITING) state = TournamentState.ONGOING;
    else if (state == TournamentState.FINISHED) // if finished we outta here
    return;
    else // not finished, ongoing still. broadcast a message to the masses
    broadcastScore();

    // todo: Check phase of previous round?
    Optional<TournamentTeam> winner = roundHolder.calculateWinner(teamManager);
    if (winner.isPresent() || !hasNextRound()) {
      unregisterAll();
      // there's a winner, return and announce them
      state = TournamentState.FINISHED;
      // if this doesn't call it just call it all normal like
      onEnd(match, winner);
      Events.callEvent(new TournamentFinishedEvent(this, winner), EventPriority.NORMAL);
      return;
    }

    cleanupRound();
    TournamentRound round = roundHolder.nextRound(match);
    addListener(round, TournamentScope.ROUND);
    round.load();
    round.start(match);
  }

  @Override
  public boolean hasNextRound() {
    if (state == TournamentState.FINISHED) return false;

    return roundHolder.hasNextRound();
  }

  @Override
  public TournamentState state() {
    return state;
  }

  @Override
  public void addRound(TournamentRound round) {
    roundHolder.addRound(round);
  }

  @Override
  public void addRoundAfterCurrent(TournamentRound round) {
    roundHolder.addRoundAfterCurrent(round);
  }

  @Override
  public Scores scores() {
    return roundHolder.scores(teamManager);
  }

  private void cleanupRound() {
    roundListeners.forEach(HandlerList::unregisterAll);
    roundListeners.clear();
  }

  @Override
  public FormattedScore currentScore() {
    return roundHolder.scores(teamManager).formattedScore(teamManager);
  }

  @Override
  public List<RoundDescription> roundsInformation(Context context) {
    return roundHolder.roundDescriptions(context);
  }

  private void broadcastScore() {
    if (!options().shouldBroadcastScore() || !roundHolder.currentRound().isScoring()) {
      return;
    }

    FormattedScore scores = roundHolder.scores(teamManager).formattedScore(teamManager);
    Bukkit.getScheduler()
        .scheduleSyncDelayedTask(
            EventsPlugin.get(),
            new Runnable() {

              @Override
              public void run() {
                Bukkit.broadcastMessage(scores.condensed());
              }
            },
            3 * 20);
  }

  public void onEnd(Match match, Optional<TournamentTeam> winner) {
    if (tournamentRoundOptions.shouldAnnounceWinner()) {
      if (winner.isPresent()) {
        Bukkit.broadcastMessage(
            teamManager.formattedName(winner.get()) + ChatColor.GOLD + " wins this round!");
      } else {
        Bukkit.broadcastMessage(ChatColor.GRAY + "This round has ended in a draw!");
      }
    }

    if (tournamentRoundOptions.shouldShutdownOnEnd()) {
      match
          .getCountdown()
          .start(new ShutdownCountdown(match), tournamentRoundOptions.shutdownDuration());
    }
  }

  @Override
  public TournamentRound currentRound() {
    return roundHolder.currentRound().currentRound();
  }
}
