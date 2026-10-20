package dev.pgm.events.format.rounds.single;

import static net.kyori.adventure.text.Component.text;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.team.TournamentTeam;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SingleRoundDescription implements RoundDescription {

  private final String mapName;
  private final SingleRound singleRound;
  private final TournamentFormat format;

  public SingleRoundDescription(String mapName, SingleRound singleRound, TournamentFormat format) {
    this.mapName = mapName;
    this.singleRound = singleRound;
    this.format = format;
  }

  @Override
  public Component roundInfo() {
    Component base =
        text("Match on ", NamedTextColor.GRAY).append(text(mapName, NamedTextColor.GOLD));
    return switch (singleRound.phase()) {
      case UNLOADED -> base;
      case WAITING -> base.append(text(" - Waiting", NamedTextColor.GRAY));
      case RUNNING ->
        base.append(text(" - ", NamedTextColor.GRAY)).append(text("Running", NamedTextColor.GREEN));
      case FINISHED -> base.append(text(" - ", NamedTextColor.GRAY)).append(winnersComponent());
    };
  }

  private Component winnersComponent() {
    Collection<? extends TournamentTeam> teams = singleRound.scores().keySet();
    if (teams.isEmpty()) {
      return text("Draw", NamedTextColor.GRAY);
    }

    List<Component> teamNames =
        teams.stream().map(x -> format.teamManager().formattedName(x)).toList();

    Component result = Component.empty();
    for (int i = 0; i < teamNames.size(); i++) {
      if (i > 0) result = result.append(text(", ", NamedTextColor.GRAY));
      result = result.append(teamNames.get(i));
    }
    return result.append(text(" won"));
  }

  @Override
  public String roundStatus() {
    return null;
  }
}
