package dev.pgm.events.format.rounds.format;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.HoverEvent.showText;

import dev.pgm.events.format.rounds.RoundDescription;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class FormatRoundDescription implements RoundDescription {

  private final FormatRound formatRound;

  public FormatRoundDescription(FormatRound formatRound) {
    this.formatRound = formatRound;
  }

  @Override
  public Component roundInfo() {
    Component base = text(
        formatRound.settings().name() + " - Best of " + formatRound.settings().bestOf());
    if (formatRound.formatTournament() != null) {
      return base.hoverEvent(showText(formatRound.formattedScore().condensed()));
    } else {
      return base.hoverEvent(showText(text("Loading...", NamedTextColor.YELLOW)));
    }
  }

  @Override
  public String roundStatus() {
    return "format round";
  }
}
