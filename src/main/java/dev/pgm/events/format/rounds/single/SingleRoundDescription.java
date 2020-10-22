package dev.pgm.events.format.rounds.single;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.team.TournamentTeam;
import java.util.Collection;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class SingleRoundDescription implements RoundDescription {

  private final String mapName;
  private final SingleRound singleRound;
  private final Tournament format;

  private final String defString;

  public SingleRoundDescription(String mapName, SingleRound singleRound, Tournament format) {
    this.mapName = mapName;
    this.singleRound = singleRound;
    this.format = format;
    this.defString = ChatColor.GRAY + "Match on " + ChatColor.GOLD + this.mapName + ChatColor.AQUA;
  }

  @Override
  public BaseComponent roundInfo() {
    switch (singleRound.phase()) {
      case UNLOADED:
        return new TextComponent(defString);
      case WAITING:
        return new TextComponent(defString + " - " + ChatColor.GRAY + "Waiting");
      case RUNNING:
        return new TextComponent(defString + " - " + ChatColor.GREEN + "Running");
      case FINISHED:
        return new TextComponent(defString + " - " + winnersString());
    }
    return new TextComponent("NULL");
  }

  private String winnersString() {
    Collection<? extends TournamentTeam> teams = singleRound.scores().keySet();
    if (teams.size() == 0) {
      // draw
      return drawString();
    }

    String teamString =
        teams.stream()
            .map(x -> format.teamManager().formattedName(x))
            .collect(Collectors.joining(ChatColor.GRAY + ", "));

    teamString += " won";
    return teamString;
  }

  private String drawString() {
    return ChatColor.GRAY + "Draw";
  }

  @Override
  public String roundStatus() {
    return null;
  }
}
