package rip.bolt.ingame.format.rounds.single;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.AbstractRound;
import rip.bolt.ingame.format.rounds.RoundDescription;
import rip.bolt.ingame.format.rounds.RoundPhase;
import rip.bolt.ingame.team.TournamentTeam;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.map.MapInfo;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.match.event.MatchFinishEvent;
import tc.oc.pgm.api.match.event.MatchLoadEvent;
import tc.oc.pgm.api.match.event.MatchStartEvent;
import tc.oc.pgm.cycle.CycleMatchModule;
import tc.oc.pgm.start.StartMatchModule;

public class SingleRound extends AbstractRound<SingleRoundOptions> {

    private final Map<TournamentTeam, Integer> scoreMap;
    private String fullMapName = "";
    private RoundDescription roundDescription;
    private RoundPhase roundPhase;

    public SingleRound(TournamentFormat format, SingleRoundOptions options) {
        super(format, options);
        this.scoreMap = new HashMap<>();

        MapInfo mapInfo = PGM.get().getMapLibrary().getMap(options.map());
        if (mapInfo == null)
            throw new IllegalArgumentException("Map " + options.map() + " wasn't found in PGM's map library. Make sure it is listed in /maps and restart the server!");
        fullMapName = mapInfo.getName();

        this.roundDescription = new SingleRoundDescription(fullMapName, this, format);
        this.roundPhase = RoundPhase.UNLOADED;
    }

    @Override
    public void load() {
        MapInfo mapInfo = PGM.get().getMapLibrary().getMap(fullMapName);
        // set message
        PGM.get().getMapOrder().setNextMap(mapInfo);
    }

    @Override
    public void start(Match match) {
        this.roundPhase = RoundPhase.WAITING;
        // cycle to the map, start in 10 seconds
        if (settings().cycleCountdown() != null)
            match.getModule(CycleMatchModule.class).startCountdown(settings().cycleCountdown());
    }

    @Override
    public void cleanup(Match match) {
        roundPhase = RoundPhase.FINISHED;
        if (!match.isFinished())
            match.setPhase(MatchPhase.FINISHED);
    }

    @Override
    public RoundPhase phase() {
        return roundPhase;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMatchEnd(MatchFinishEvent event) {
        this.roundPhase = RoundPhase.FINISHED;

        if (event.getWinners().size() != event.getMatch().getCompetitors().size())
            event.getWinners().stream().map(x -> tournament().teamManager().tournamentTeam(x)).filter(Optional::isPresent).map(Optional::get).forEach(x -> scoreMap.put(x, settings().score()));

        tournament().nextRound(event.getMatch());
    }

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
        this.roundPhase = RoundPhase.RUNNING;
    }

    @EventHandler
    public void matchCycleEvent(MatchLoadEvent event) {
        // force start 5 minute countdown
        if (settings().startCountdown() != null)
            event.getMatch().needModule(StartMatchModule.class).forceStartCountdown(settings().startCountdown(), Duration.ZERO);
    }

    @Override
    public Map<TournamentTeam, Integer> scores() {
        return scoreMap;
    }

    @Override
    public RoundDescription describe() {
        return roundDescription;
    }
}
