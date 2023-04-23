package dev.pgm.events.ready;

import static net.kyori.adventure.text.Component.text;

import dev.pgm.events.config.AppData;
import dev.pgm.events.utils.JoinUtils;
import dev.pgm.events.utils.Response;
import java.time.Duration;
import javax.annotation.Nullable;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.match.MatchPhase;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.CountdownStartEvent;
import tc.oc.pgm.match.ObserverParty;
import tc.oc.pgm.start.StartCountdown;
import tc.oc.pgm.start.StartMatchModule;
import tc.oc.pgm.util.named.NameStyle;

public class ReadyManagerImpl implements ReadyManager {

  private final ReadyParties parties;
  private final ReadySystem system;

  public ReadyManagerImpl(ReadySystem system, ReadyParties parties) {
    this.system = system;
    this.parties = parties;
  }

  public void createMatchStart(Match match) {
    createMatchStart(match, Duration.ofSeconds(20));
  }

  public void createMatchStart(Match match, Duration duration) {
    match.needModule(StartMatchModule.class).forceStartCountdown(duration, Duration.ZERO);
  }

  @Override
  public void ready(Party party, @Nullable MatchPlayer player) {
    Match match = party.getMatch();

    TextComponent.Builder message =
        text()
            .append(party.getName())
            .append(text(" marked as ").append(text("ready", NamedTextColor.GREEN)));
    if (player != null) message.append(text(" by ").append(player.getName(NameStyle.COLOR)));

    match.sendMessage(message);

    parties.ready(party);
    if (allReady(match)) {
      createMatchStart(match);
    }
  }

  @Override
  public void unready(Party party, @Nullable MatchPlayer player) {
    Match match = party.getMatch();

    TextComponent.Builder message =
        text()
            .append(party.getName())
            .append(text(" marked as ").append(text("unready", NamedTextColor.RED)));
    if (player != null) message.append(text(" by ").append(player.getName(NameStyle.COLOR)));

    match.sendMessage(message);

    if (allReady(match) && system.unreadyShouldCancel()) {
      // check if unready should cancel
      createMatchStart(match, system.getResetDuration());
    }

    parties.unready(party);
  }

  @Override
  public boolean isReady(Party party) {
    return parties.isReady(party);
  }

  @Override
  public boolean allReady(Match match) {
    return parties.allReady(match);
  }

  @Override
  public Response canReady(Match match) {
    if (!match.getPhase().canTransitionTo(MatchPhase.RUNNING)) {
      return Response.deny(text("You are not able use this command during a match!"));
    }

    if (!system.canReady()) {
      return Response.deny(text("You are not able to ready at this time!"));
    }

    return Response.allow();
  }

  @Override
  public Response canReady(Party party) {
    return canReady(party, true);
  }

  @Override
  public Response canUnready(Party party) {
    return canReady(party, false);
  }

  public Response canReady(Party party, boolean state) {
    if (isReady(party) == state) {
      return Response.deny(text("You are already " + (state ? "ready" : "unready") + "!"));
    }

    if (state && AppData.readyFullTeamRequired() && !JoinUtils.isPartyFull(party)) {
      return Response.deny(text("You can not ready until your team is full!"));
    }

    return Response.allow();
  }

  @Override
  public Response canReady(MatchPlayer player) {
    return canReady(player, true);
  }

  @Override
  public Response canUnready(MatchPlayer player) {
    return canReady(player, false);
  }

  public Response canReady(MatchPlayer player, boolean state) {
    Match match = player.getMatch();
    Party party = player.getParty();

    if (party instanceof ObserverParty) {
      if (!AppData.observersMustReady()) {
        return Response.deny(text("Observers are not allowed to use this command!"));
      }

      if (!player.getBukkit().hasPermission("events.staff")) {
        return Response.deny(text("You do not have permission to use this command!"));
      }
    }

    Response matchResponse = canReady(match);
    if (matchResponse.isDenied()) {
      return matchResponse;
    }

    Response teamResponse = canReady(party, state);
    if (teamResponse.isDenied()) {
      return teamResponse;
    }

    return Response.allow();
  }

  @Override
  public void reset() {
    parties.reset();
    system.reset();
  }

  @Override
  public void handleCountdownStart(CountdownStartEvent event) {
    Match match = event.getMatch();
    Duration remaining = ((StartCountdown) event.getCountdown()).getRemaining();
    system.onStart(remaining, allReady(match));
  }
}
