package dev.pgm.events.format.score;

import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.utils.Pair;
import java.util.Collection;
import org.bukkit.ChatColor;

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
  public String topLine() {
    String firstName = teamManager.formattedName(topTwo.first.team());
    String secondName = teamManager.formattedName(topTwo.second.team());

    firstName = addPadding(firstName, secondName);
    secondName = addPadding(secondName, firstName);
    return firstName + ChatColor.GRAY + " - " + secondName;
  }

  private String addPadding(String target, String otherString) {
    if (target.length() < otherString.length())
      for (int i = 0; i < otherString.length() - target.length(); i++) target = " " + target;

    return target;
  }

  @Override
  public String bottomLine() {
    String bott = "";
    bott += teamManager.teamColour(topTwo.first.team());
    if (justWon.contains(topTwo.first.team())) {
      bott += ChatColor.BOLD;
      bott += ChatColor.UNDERLINE;
    }
    bott += topTwo.first.score() + ChatColor.RESET.toString() + ChatColor.GRAY + " - ";
    bott += teamManager.teamColour(topTwo.second.team());
    if (justWon.contains(topTwo.second.team())) {
      bott += ChatColor.BOLD;
      bott += ChatColor.UNDERLINE;
    }
    bott += topTwo.second.score();
    return bott;
  }

  @Override
  public String condensed() {
    return teamManager.formattedName(topTwo.first.team())
        + " "
        + ChatColor.WHITE
        + +topTwo.first.score()
        + ChatColor.GRAY
        + " - "
        + ChatColor.WHITE
        + topTwo.second.score()
        + " "
        + teamManager.formattedName(topTwo.second.team());
  }
}
