package dev.pgm.events.format.rounds.veto;

import dev.pgm.events.format.rounds.veto.settings.VetoOption;
import dev.pgm.events.format.rounds.veto.settings.VetoSettings;
import dev.pgm.events.team.TournamentTeam;
import org.jetbrains.annotations.Nullable;

public class VetoHistory {

  private final TournamentTeam team;
  private final VetoSettings.VetoType vetoType;
  private final VetoOption chosen;

  private final boolean shouldAnnounce;

  public VetoHistory(
      @Nullable TournamentTeam team,
      VetoSettings.VetoType vetoType,
      VetoOption chosen,
      boolean shouldAnnounce) {
    this.team = team;
    this.vetoType = vetoType;
    this.chosen = chosen;
    this.shouldAnnounce = shouldAnnounce;
  }

  public @Nullable TournamentTeam team() {
    return team;
  }

  public VetoSettings.VetoType vetoType() {
    return vetoType;
  }

  public VetoOption optionChosen() {
    return chosen;
  }

  public boolean shouldAnnounce() {
    return shouldAnnounce;
  }
}
