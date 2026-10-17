package dev.pgm.events.format.rounds.veto;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.veto.settings.VetoOption;
import dev.pgm.events.format.rounds.veto.settings.VetoSettings;
import dev.pgm.events.team.TournamentTeam;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class VetoDescription implements RoundDescription {

  private final VetoRound vetoRound;
  private final TournamentFormat tournamentFormat;

  public VetoDescription(TournamentFormat format, VetoRound vetoRound) {
    this.vetoRound = vetoRound;
    this.tournamentFormat = format;
  }

  @Override
  public Component roundInfo() {
    return switch (vetoRound.phase()) {
      case UNLOADED -> Component.text("Waiting to begin veto process", NamedTextColor.GRAY);
      case WAITING ->
        Component.text("Deciding veto order with veto decider", NamedTextColor.GREEN)
            .hoverEvent(
                HoverEvent.showText(tournamentFormat.currentRound().describe().roundInfo()));
      case RUNNING ->
        Component.text("Veto process is running now", NamedTextColor.GREEN)
            .hoverEvent(HoverEvent.showText(formattedVetosSoFar()));
      case FINISHED ->
        Component.text("Veto process has concluded", NamedTextColor.GRAY)
            .hoverEvent(HoverEvent.showText(formattedVetosSoFar()));
    };
  }

  private Component formattedVetosSoFar() {
    List<VetoHistory> history = vetoRound.vetoHistory();
    Component result = Component.empty();
    for (int i = 0; i < history.size(); i++) {
      if (i > 0) result = result.append(Component.newline());
      result = result.append(historyLine(i, history.get(i)));
    }
    return result;
  }

  private Component historyLine(int index, VetoHistory history) {
    return Component.text(index + 1 + ". ", NamedTextColor.GOLD)
        .append(formatHistoryComponent(history));
  }

  public Component formatHistoryComponent(VetoHistory history) {
    Component prefix;
    if (history.team() == null) {
      prefix = Component.text("RANDOM", NamedTextColor.GOLD);
    } else {
      prefix = tournamentFormat.teamManager().formattedName(history.team());
    }
    prefix = prefix.append(Component.text(" has ", NamedTextColor.GRAY));

    Component action;
    if (history.vetoType() == VetoSettings.VetoType.BAN) {
      action = Component.text("BANNED ", NamedTextColor.RED);
    } else {
      action = Component.text("SELECTED ", NamedTextColor.GREEN);
    }

    return prefix
        .append(action)
        .append(Component.text(history.optionChosen().name(), NamedTextColor.GOLD));
  }

  public String countdown(TournamentTeam picking, VetoSettings.VetoType type) {
    return picking.getName() + " choose an option to " + actionWord(type);
  }

  private String actionWord(VetoSettings.VetoType type) {
    return switch (type) {
      case BAN -> "BAN";
      case CHOOSE_FIRST, CHOOSE_LAST -> "PLAY";
    };
  }

  public Component optionsHeader(VetoSettings.VetoType type) {
    return Component.text("Choose an option to ", NamedTextColor.GRAY)
        .append(actionComponent(type));
  }

  public Component commandPrompt(VetoSettings.VetoType type) {
    return actionComponent(type)
        .append(Component.text(" with: ", NamedTextColor.GRAY))
        .append(Component.text("/veto <number>", NamedTextColor.GOLD));
  }

  public List<Component> formatOptions(List<VetoOption> options, VetoSettings.VetoType type) {
    List<Component> comps = new ArrayList<>(options.size());
    for (int i = 0; i < options.size(); i++) {
      comps.add(Component.text((i + 1) + ". ", NamedTextColor.GOLD)
          .append(Component.text(options.get(i).name(), NamedTextColor.AQUA)));
    }
    return comps;
  }

  private Component actionComponent(VetoSettings.VetoType type) {
    return switch (type) {
      case BAN -> Component.text("BAN", NamedTextColor.RED);
      case CHOOSE_FIRST, CHOOSE_LAST -> Component.text("PLAY", NamedTextColor.GREEN);
    };
  }

  @Override
  public String roundStatus() {
    return "NULL - VETO";
  }
}
