package dev.pgm.events.format.rounds.format;

import dev.pgm.events.format.rounds.RoundDescription;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class FormatRoundDescription implements RoundDescription {

  private final FormatRound formatRound;

  public FormatRoundDescription(FormatRound formatRound) {
    this.formatRound = formatRound;
  }

  @Override
  public Component roundInfo() {
    Component base = Component.text(
        formatRound.settings().name() + " - Best of " + formatRound.settings().bestOf());
    if (formatRound.formatTournament() != null) {
      return base.hoverEvent(HoverEvent.showText(formatRound.formattedScore().condensed()));
    } else {
      return base.hoverEvent(
          HoverEvent.showText(Component.text("Loading...", NamedTextColor.YELLOW)));
    }
  }

  @Override
  public String roundStatus() {
    return "format round";
  }
}
