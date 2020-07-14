package rip.bolt.ingame;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.md_5.bungee.api.ChatColor;
import rip.bolt.ingame.api.definitions.BoltMatch;
import rip.bolt.ingame.api.definitions.Team;
import rip.bolt.ingame.format.RoundReferenceHolder;
import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.TournamentFormatImpl;
import rip.bolt.ingame.format.TournamentRoundOptions;
import rip.bolt.ingame.format.rounds.single.SingleRound;
import rip.bolt.ingame.format.rounds.single.SingleRoundOptions;
import rip.bolt.ingame.format.winner.BestOfCalculation;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.party.Competitor;

public class RankedManager implements Listener {

    private boolean cycledToRightMap;

    private TournamentFormat format;
    private BoltMatch match;

    private Runnable pollTask;
    private int pollTaskId = -1;

    public RankedManager() {
        pollTask = new Runnable() {
            
            @Override
            public void run() {
                if (setupMatch()) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "A new match is starting on this server!");
                    format.nextRound(PGM.get().getMatchManager().getMatches().next());
                }
            }
        };

        // fetch match
        setupMatch();

        // we use an async task otherwise the server will not start
        // pgm loads the world in the main thread using a task
        // createMatch(String).get() is blocking
        // bukkit won't be able to complete the load world task on the main thread
        // since this task will be blocking the main thread
        Bukkit.getScheduler().runTaskAsynchronously(Tournament.get(), new Runnable() {

            @Override
            public void run() {
                try {
                    PGM.get().getMatchManager().createMatch(null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public boolean setupMatch() {
        match = Tournament.get().getApiManager().fetchMatchData();
        if (match == null) {
            if (!cycledToRightMap && pollTaskId == -1) // we haven't played a game on this server yet
                pollTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Tournament.get(), pollTask, 15 * 20, 15 * 20); // every 15 seconds
            return false;
        }

        if (pollTaskId != -1) {
            Bukkit.getScheduler().cancelTask(pollTaskId);
            pollTaskId = -1;
        }

        Tournament.get().getTeamManager().clear();
        for (Team team : match.getTeams())
            Tournament.get().getTeamManager().addTeam(team);

        format = new TournamentFormatImpl(Tournament.get().getTeamManager(), new TournamentRoundOptions(false, false, true, Duration.ofMinutes(30), Duration.ofSeconds(30), Duration.ofSeconds(40), new BestOfCalculation<>(1)), new RoundReferenceHolder());
        SingleRound ranked = new SingleRound(format, new SingleRoundOptions("ranked", Duration.ofSeconds(5), Duration.ofSeconds(300), match.getMap(), 1, true, true));
        format.addRound(ranked);

        return true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMatchLoad(MatchLoadEvent event) {
        if (!cycledToRightMap && format != null) {
            format.nextRound(event.getMatch());
            cycledToRightMap = true;
        }
    }

    @EventHandler
    public void onMatchFinish(MatchFinishEvent event) {
        for (Competitor winner : event.getWinners())
            match.getWinners().add(winner.getNameLegacy());

        // run async to stop server lag
        Bukkit.getScheduler().runTaskAsynchronously(Tournament.get(), new Runnable() {

            @Override
            public void run() {
                Tournament.get().getApiManager().postMatchData(match);
            }

        });

        pollTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Tournament.get(), pollTask, 30 * 20, 30 * 20); // every 30 seconds
    }

    @EventHandler
    public void onPlayerRunCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/tm") || event.getMessage().toLowerCase().startsWith("/tm ") || event.getMessage().equalsIgnoreCase("/tourney") || event.getMessage().toLowerCase().startsWith("/tourney ") || event.getMessage().equalsIgnoreCase("/tournament") || event.getMessage().toLowerCase().startsWith("/tournament ")) {
            event.getPlayer().sendMessage(ChatColor.RED + "This command is disabled in Ranked.");
            event.setCancelled(true);
        }
    }

    public void poll() {
        pollTask.run();
    }

}
