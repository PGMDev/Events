package dev.pgm.events.format;

import dev.pgm.events.format.rounds.TournamentRound;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RoundReferenceHolder {

  private final Map<String, TournamentRound> roundMap;

  public RoundReferenceHolder() {
    this.roundMap = new HashMap<>();
  }

  void registerRound(String id, TournamentRound round) {
    // gonna let it override same id by default, this will be the intended behaviour and allow some
    // things to be easier
    /*if(roundMap.containsKey(id)) {
        //improve debug system
        throw new IllegalStateException("Attempted to register a round with id of " + id);
    }*/

    roundMap.put(id, round);
  }

  public TournamentRound needRound(String id) {
    return roundMap.get(id);
  }

  public Optional<TournamentRound> round(String id) {
    if (roundMap.containsKey(id)) return Optional.of(roundMap.get(id));

    return Optional.empty();
  }
}
