package dev.pgm.events.ready;

import dev.pgm.events.utils.Response;
import javax.annotation.Nullable;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.party.Party;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.events.CountdownStartEvent;

public interface ReadyManager {

  void ready(Party party, @Nullable MatchPlayer player);

  void unready(Party party, @Nullable MatchPlayer player);

  boolean isReady(Party party);

  boolean allReady(Match match);

  Response canReady(Match match);

  Response canReady(Party party);

  Response canUnready(Party party);

  Response canReady(MatchPlayer player);

  Response canUnready(MatchPlayer player);

  void handleCountdownStart(CountdownStartEvent event);

  void reset();
}
