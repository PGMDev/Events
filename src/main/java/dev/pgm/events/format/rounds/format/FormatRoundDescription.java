package dev.pgm.events.format.rounds.format;

import dev.pgm.events.format.rounds.RoundDescription;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class FormatRoundDescription implements RoundDescription {

  private final FormatRound formatRound;

  public FormatRoundDescription(FormatRound formatRound) {
    this.formatRound = formatRound;
  }

  @Override
  public BaseComponent roundInfo() {
    TextComponent component =
        new TextComponent(
            formatRound.settings().name() + " - Best of " + formatRound.settings().bestOf());
    if (formatRound.formatTournament() != null)
      component.setHoverEvent(
          new HoverEvent(
              HoverEvent.Action.SHOW_TEXT,
              new BaseComponent[] {new TextComponent(formatRound.formattedScore().condensed())}));
    else
      component.setHoverEvent(
          new HoverEvent(
              HoverEvent.Action.SHOW_TEXT,
              new BaseComponent[] {new TextComponent(ChatColor.YELLOW + "Loading...")}));

    return component;
  }

  @Override
  public String roundStatus() {
    return "format round";
  }
}
