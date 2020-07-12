package rip.bolt.ingame.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import rip.bolt.ingame.Tournament;
import rip.bolt.ingame.context.Context;
import rip.bolt.ingame.format.events.TournamentFinishedEvent;
import rip.bolt.ingame.format.rounds.RoundDescription;
import rip.bolt.ingame.format.rounds.TournamentRound;
import rip.bolt.ingame.format.score.FormattedScore;
import rip.bolt.ingame.format.score.Scores;
import rip.bolt.ingame.format.shutdown.ShutdownCountdown;
import rip.bolt.ingame.team.TournamentTeam;
import rip.bolt.ingame.team.TournamentTeamManager;
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

    public TournamentFormatImpl(TournamentTeamManager teamManager, TournamentRoundOptions options, RoundReferenceHolder references) {
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
        if (scope == TournamentScope.ROUND)
            roundListeners.add(listener);
        else
            permanentListeners.add(listener);

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
        if (state == TournamentState.WAITING)
            state = TournamentState.ONGOING;
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
        if (state == TournamentState.FINISHED)
            return false;

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
        // new GlobalAudience().sendTitle(roundHolder.scores(teamManager).formattedScore(teamManager), roundHolder.currentRound().describe().roundStatus(), 5);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Tournament.get(), new Runnable() {
           
            @Override
            public void run() {
                // GlobalAudience glob = new GlobalAudience();
                // removed the team from the title, add in with scores.topLine() (needs re-naming)
                // glob.sendTitle(scores.bottomLine(), "", 4);
                // glob.sendMessageRaw(scores.condensed());

                Bukkit.broadcastMessage(scores.condensed());
            }
            
        }, 3 * 20);
        // new GlobalAudience().sendTitle(scores.topLine(), scores.bottomLine(), 5);
    }

    public void onEnd(Match match, Optional<TournamentTeam> winner) {
        // System.out.println(winner);
        winner.ifPresent(x -> Bukkit.broadcastMessage(x.getName()));

        if (tournamentRoundOptions.shouldAnnounceWinner()) {
            if (winner.isPresent()) {
                // new GlobalAudience().sendMessage("tournament.announcement.winner", winner.get().teamName());
                // System.out.println(winner.get().teamName());
                // StratusAPI.get().newChain().delay(8, TimeUnit.SECONDS).syncLast((x) -> {
                // GlobalAudience glob = new GlobalAudience();
                // removed the team from the title, add in with scores.topLine() (needs re-naming)
                // glob.sendTitle("", teamManager.formattedName(winner.get()) + ChatColor.GOLD + " wins this round!", 4);
                // }).execute();
                Bukkit.broadcastMessage(teamManager.formattedName(winner.get()) + ChatColor.GOLD + " wins this round!");
            } else {
                // new GlobalAudience().sendMessage("tournament.announcement.draw");
                // StratusAPI.get().newChain().delay(8, TimeUnit.SECONDS).syncLast((x) -> {
                // GlobalAudience glob = new GlobalAudience();
                // removed the team from the title, add in with scores.topLine() (needs re-naming)
                // glob.sendTitle("", ChatColor.GRAY + "This round has ended in a draw!", 4);
                // }).execute();
                Bukkit.broadcastMessage(ChatColor.GRAY + "This round has ended in a draw!");
            }
        }

        if (tournamentRoundOptions.shouldShutdownOnEnd()) {
            match.getCountdown().start(new ShutdownCountdown(match), tournamentRoundOptions.shutdownDuration());
        }
    }

    @Override
    public TournamentRound currentRound() {
        return roundHolder.currentRound().currentRound();
    }

}
