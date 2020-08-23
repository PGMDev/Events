package dev.pgm.events.format.rounds.veto;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.veto.settings.VetoOption;
import dev.pgm.events.format.rounds.veto.settings.VetoSettings;
import dev.pgm.events.team.TournamentTeam;
import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class VetoDescription implements RoundDescription {

  private final VetoRound vetoRound;
  private final TournamentFormat tournamentFormat;

  public VetoDescription(TournamentFormat format, VetoRound vetoRound) {
    this.vetoRound = vetoRound;
    this.tournamentFormat = format;
  }

  @Override
  public BaseComponent roundInfo() {
    switch (vetoRound.phase()) {
      case UNLOADED:
        return new TextComponent(ChatColor.GRAY + "Waiting to begin veto process");
      case WAITING:
        TextComponent waiting =
            new TextComponent(ChatColor.GREEN + "Deciding veto order with veto decider");
        waiting.setHoverEvent(
            new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new BaseComponent[] {tournamentFormat.currentRound().describe().roundInfo()}));
        return waiting;
      case RUNNING:
        TextComponent running = new TextComponent(ChatColor.GREEN + "Veto process is running now");
        running.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, formattedVetosSoFar()));
        return running;
      case FINISHED:
        TextComponent finished = new TextComponent(ChatColor.GRAY + "Veto process has concluded");
        finished.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, formattedVetosSoFar()));
        return finished;
    }
    // not gonna get here
    throw new IllegalStateException("New RoundPhase added without editing the veto settings!");
  }

  private BaseComponent[] formattedVetosSoFar() {
    List<VetoHistory> history = vetoRound.vetoHistory();
    int vetoCount = history.size();

    BaseComponent[] messages = new BaseComponent[vetoCount];
    for (int i = 0; i < history.size() - 1; i++) {
      messages[i] = new TextComponent(fromHistory(i, true, history.get(i)));
    }

    if (history.size() > 0) {
      // add last line with no new line after
      messages[vetoCount - 1] =
          new TextComponent(fromHistory(vetoCount - 1, false, history.get(vetoCount - 1)));
    }

    return messages;
  }

  private String fromHistory(int index, boolean newLine, VetoHistory history) {
    String line = ChatColor.GOLD + Integer.toString(index + 1) + ". " + formatHistory(history);
    if (newLine) {
      line += "\n";
    }
    return line;
  }

  public String formatHistory(VetoHistory history) {
    String prefix = "";
    if (history.team() == null) {
      prefix = ChatColor.GOLD + "RANDOM";
    } else {
      prefix = tournamentFormat.teamManager().formattedName(history.team());
    }
    prefix += ChatColor.GRAY + " has ";

    if (history.vetoType() == VetoSettings.VetoType.BAN) {
      prefix += ChatColor.RED + "BANNED ";
    } else {
      prefix += ChatColor.GREEN + "SELECTED ";
    }

    return prefix + ChatColor.GOLD + history.optionChosen().name();
  }

  public String countdown(TournamentTeam picking, VetoSettings.VetoType type) {
    return tournamentFormat.teamManager().formattedName(picking)
        + ChatColor.GRAY
        + " choose an option to "
        + actionWord(type);
  }

  private String actionWord(VetoSettings.VetoType type) {
    switch (type) {
      case BAN:
        return ChatColor.RED + "BAN";
      case CHOOSE_FIRST:
      case CHOOSE_LAST:
        return ChatColor.GREEN + "PLAY";
    }
    return "NULL_ACTION";
  }

  public String optionsHeader(VetoSettings.VetoType type) {
    return ChatColor.GRAY + "Choose an option to " + actionWord(type);
  }

  public BaseComponent commandPrompt(VetoSettings.VetoType type) {
    return new TextComponent(
        actionWord(type) + ChatColor.GRAY + " with: " + ChatColor.GOLD + "/veto <number>");
    // team.sendMessage(ChatColor.GOLD + "Veto with: " + ChatColor.GRAY + "/events veto <number>");
  }

  public List<BaseComponent[]> formatOptions(List<VetoOption> options, VetoSettings.VetoType type) {
    List<BaseComponent[]> comps = new ArrayList<>(options.size());

    for (int i = 0; i < options.size(); i++) {
      int vetoNumber = i + 1;
      VetoOption option = options.get(i);
      ComponentBuilder comp =
          new ComponentBuilder(
              ChatColor.GOLD
                  + Integer.toString(vetoNumber)
                  + ". "
                  + ChatColor.AQUA
                  + option.name());

      /*comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
              new TextComponent(ChatColor.GRAY + "Click to " + actionWord(type))
      }));
      comp.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/events veto " + vetoNumber));*/
      comps.add(comp.create());
    }

    return comps;
  }

  @Override
  public String roundStatus() {
    return "NULL - VETO";
  }
}
