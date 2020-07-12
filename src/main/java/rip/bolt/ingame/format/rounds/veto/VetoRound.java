package rip.bolt.ingame.format.rounds.veto;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.TournamentRoundOptions;
import rip.bolt.ingame.format.rounds.AbstractRound;
import rip.bolt.ingame.format.rounds.RoundPhase;
import rip.bolt.ingame.format.rounds.TournamentRound;
import rip.bolt.ingame.format.rounds.veto.settings.VetoSettings;
import rip.bolt.ingame.format.rounds.vetoselector.VetoSelectorRound;
import rip.bolt.ingame.format.winner.BestOfCalculation;
import rip.bolt.ingame.team.TournamentTeam;
import tc.oc.pgm.api.match.Match;

public class VetoRound extends AbstractRound<VetoSettings> {

    private final VetoDescription description;

    // format used for veto decider (tournament in a tournament?!?!)
    private TournamentFormat vetoDecider;
    private VetoController controller;
    private TournamentTeam selectingTeam; // the team that chose this veto (if it was vetoed)

    public VetoRound(TournamentFormat format, VetoSettings vetoSettings) {
        super(format, vetoSettings);
        this.description = new VetoDescription(format, this);
    }

    @Override
    public VetoDescription describe() {
        return description;
    }

    @Override
    public void load() {
        setPhase(RoundPhase.WAITING);
        vetoDecider = new VetoTournamentImpl(tournament().teamManager(), new TournamentRoundOptions(false, false, false, Duration.ofSeconds(20), Duration.ofSeconds(30), Duration.ofSeconds(40), new BestOfCalculation<>(1)), this, tournament().references());

        TournamentRound decider = settings().decider().newRound(vetoDecider);
        if (decider instanceof VetoSelectorRound)
            ((VetoSelectorRound) decider).setSelectingTeam(selectingTeam);
        vetoDecider.addRound(decider);
    }

    @Override
    public void start(Match match) {
        vetoDecider.nextRound(match);
    }

    @Override
    public void cleanup(Match match) {
        vetoDecider.unregisterAll();
        setPhase(RoundPhase.FINISHED);

        // cancel all countdowns
        match.getCountdown().cancelAll(VetoCountdown.class);
        // remove reference to decider to let it be garbage collected
        vetoDecider = null;
    }

    public void startVeto(Match match, List<TournamentTeam> vetoOrder) {
        setPhase(RoundPhase.RUNNING);
        this.controller = new VetoController(tournament(), settings(), vetoOrder);
        cycleVeto(match);
    }

    private void endVeto(Match match) {
        setPhase(RoundPhase.FINISHED);
        // controller.forRemaining(settings().defType());
        controller.completeVetos();
        tournament().addRoundAfterCurrent(controller.toPlay());

        // move the below formatting to the description class
        Bukkit.broadcastMessage(ChatColor.GOLD + "------ " + ChatColor.AQUA + "Veto Finished" + ChatColor.GOLD + " ------");
        for (int i = 0; i < controller.toPlay().size(); i++)
            Bukkit.broadcast(new TextComponent(ChatColor.GOLD + Integer.toString(i + 1) + ". "), controller.toPlay().get(i).describe().roundInfo());

        tournament().nextRound(match);
    }

    private void cycleVeto(Match match) {
        // cycle to next veto
        // TournamentTeam team = controller.picking();
        // team.sendMessage(description.optionsHeader(controller.currentType())); for (BaseComponent[] comp : description.formatOptions(controller.remainingOptions(), controller.currentType())) { team.sendMessage(comp); }
        
        Optional<TournamentTeam> picking = picking();
        if (!picking.isPresent()) {
            tryAutoVeto(match);
            return;
        }

        match.getCountdown().start(new VetoCountdown(match, this, description.countdown(picking.get(), controller.currentType()), controller), controller.currentVetoDuration());
    }

    public boolean canVeto() {
        return controller.hasMoreVetoing();
    }

    public List<VetoHistory> vetoHistory() {
        return controller.history();
    }

    public Optional<TournamentTeam> picking() {
        return controller.picking();
    }

    public boolean validVetoNumber(int number) {
        return controller.validVetoNumber(number);
    }

    public void randomVeto(Match match) {
        Optional<TournamentTeam> picking = picking();
        if (!picking.isPresent()) {
            tryAutoVeto(match);
            return;
        }

        // force a random veto
        veto(match, picking.get(), randomOption());
    }

    private int randomOption() {
        Random rand = new Random();
        return rand.nextInt(controller.remainingOptions().size());
    }

    private void tryAutoVeto(Match match) {
        Optional<TournamentTeam> picking = picking();
        if (picking.isPresent())
            return;

        // doing an auto veto, therefore team is null
        doVeto(match, null, randomOption());
    }

    private void doVeto(Match match, TournamentTeam team, int pick) {
        match.getCountdown().cancelAll(VetoCountdown.class);
        VetoHistory history = controller.veto(team, pick);
        if (history.shouldAnnounce())
            Bukkit.broadcastMessage(description.formatHistory(history));

        // maybe move this stuff away?
        if (!canVeto())
            endVeto(match);
        else // cycle veto
            cycleVeto(match);
    }

    public void veto(Match match, TournamentTeam team, int pick) {
        // some quick checks before doing the veto
        if (phase() != RoundPhase.RUNNING)
            return;

        if (!controller.isPicking(team) && canVeto())
            return;

        doVeto(match, team, pick);
    }

    @Override
    public TournamentRound currentRound() {
        if (phase() == RoundPhase.RUNNING || phase() == RoundPhase.FINISHED)
            return this;

        if (vetoDecider != null)
            return vetoDecider.currentRound();

        // return this if format is null
        return this;
    }

    public TournamentTeam getSelectingTeam() {
        return selectingTeam;
    }

    public void setSelectingTeam(TournamentTeam selectingTeam) {
        this.selectingTeam = selectingTeam;
    }

}
