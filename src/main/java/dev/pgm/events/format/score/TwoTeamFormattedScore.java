package dev.pgm.events.format.score;

import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.utils.Pair;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class TwoTeamFormattedScore implements FormattedScore {

  private final TournamentTeamManager teamManager;
  private final Pair<Score, Score> topTwo;

  private final Collection<? extends TournamentTeam> justWon;

  public TwoTeamFormattedScore(
      TournamentTeamManager teamManager,
      Pair<Score, Score> pair,
      Collection<? extends TournamentTeam> justWon) {
    this.teamManager = teamManager;
    this.topTwo = pair;
    this.justWon = justWon;
  }

  @Override
  public Component topLine() {
    return teamManager
        .formattedName(topTwo.first.team())
        .append(Component.text(" - ", NamedTextColor.GRAY))
        .append(teamManager.formattedName(topTwo.second.team()));
  }

  @Override
  public Component bottomLine() {
    Component first =
        Component.text(topTwo.first.score(), teamManager.teamColour(topTwo.first.team()));
    if (justWon.contains(topTwo.first.team())) {
      first = first.decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED);
    }

    Component second =
        Component.text(topTwo.second.score(), teamManager.teamColour(topTwo.second.team()));
    if (justWon.contains(topTwo.second.team())) {
      second = second.decorate(TextDecoration.BOLD).decorate(TextDecoration.UNDERLINED);
    }

    return first.append(Component.text(" - ", NamedTextColor.GRAY)).append(second);
  }

  @Override
  public Component condensed() {
    return teamManager
        .formattedName(topTwo.first.team())
        .append(Component.space())
        .append(Component.text(topTwo.first.score(), NamedTextColor.WHITE))
        .append(Component.text(" - ", NamedTextColor.GRAY))
        .append(Component.text(topTwo.second.score(), NamedTextColor.WHITE))
        .append(Component.space())
        .append(teamManager.formattedName(topTwo.second.team()));
  }
}
