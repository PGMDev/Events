package dev.pgm.events.format.rounds.veto;

import static tc.oc.pgm.lib.net.kyori.adventure.text.Component.text;

import dev.pgm.events.team.TournamentTeam;
import java.time.Duration;
import java.util.Optional;
import net.md_5.bungee.api.chat.BaseComponent;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.countdowns.MatchCountdown;
import tc.oc.pgm.lib.net.kyori.adventure.text.Component;

public class VetoCountdown extends MatchCountdown {

  private final VetoRound vetoRound;
  private final VetoController controller;
  private final String message;

  public VetoCountdown(
      Match match, VetoRound vetoRound, String message, VetoController controller) {
    super(match);
    this.vetoRound = vetoRound;
    this.controller = controller;
    this.message = message;
  }

  @Override
  protected Component formatText() {
    return text(message);
  }

  @Override
  public void onTick(Duration remaining, Duration total) {
    super.onTick(remaining, total);
    long secondsLeft = remaining.getSeconds();
    long origSeconds = total.getSeconds();

    if ((secondsLeft <= 10 && secondsLeft % 5 == 0)
        || (secondsLeft % 30 == 0 && origSeconds - secondsLeft > 20)) // announce it
    announceRemaining();
  }

  @Override
  public void onStart(Duration remaining, Duration total) {
    super.onStart(remaining, total);
    announceRemaining();
  }

  private void announceRemaining() {
    Optional<TournamentTeam> teamOpt = controller.picking();
    if (!teamOpt.isPresent()) return;

    TournamentTeam team = teamOpt.get();
    team.sendMessage(vetoRound.describe().optionsHeader(controller.currentType()));
    for (BaseComponent[] comp :
        vetoRound.describe().formatOptions(controller.remainingOptions(), controller.currentType()))
      team.sendMessage(comp);

    team.sendMessage(vetoRound.describe().commandPrompt(controller.currentType()));
  }

  @Override
  public void onEnd(Duration total) {
    super.onEnd(total);
    vetoRound.randomVeto(match);
  }
}
