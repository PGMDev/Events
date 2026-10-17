package dev.pgm.events.commands;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.RoundDescription;
import dev.pgm.events.format.rounds.format.FormatTournamentImpl;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Command;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.CommandDescription;
import tc.oc.pgm.util.Audience;

@Command("tourney|tournament|tm|events")
public class TournamentUserCommands {

  @Command("score")
  @CommandDescription("Shows the current score in the tournament")
  public void currentScore(CommandSender sender, TournamentFormat format) {
    Audience audience = Audience.get(sender);
    if (format instanceof FormatTournamentImpl) {
      String formatName =
          ((FormatTournamentImpl) format).getFormatRound().settings().name();
      audience.sendMessage(Component.text("For " + formatName + ":", NamedTextColor.YELLOW));
      audience.sendMessage(format.currentScore().condensed());

      Optional<TournamentFormat> parentOptional =
          EventsPlugin.get().getTournamentManager().currentTournament();
      if (parentOptional.isPresent()) {
        audience.sendMessage(
            Component.text("Overall score (excluding " + formatName + "):", NamedTextColor.YELLOW));
        audience.sendMessage(parentOptional.get().currentScore().condensed());
      }
    } else {
      audience.sendMessage(format.currentScore().condensed());
    }
  }

  @Command("rounds")
  @CommandDescription("Shows the rounds from this event")
  public void rounds(CommandSender sender, TournamentFormat format) {
    String header = "Event Rounds";
    if (format instanceof FormatTournamentImpl)
      header +=
          " (" + ((FormatTournamentImpl) format).getFormatRound().settings().name() + ")";

    Audience audience = Audience.get(sender);
    audience.sendMessage(Component.text("------- ", NamedTextColor.GOLD)
        .append(Component.text(header, NamedTextColor.AQUA))
        .append(Component.text(" -------", NamedTextColor.GOLD)));

    int round = 1;
    for (RoundDescription roundDescription : format.roundsInformation()) {
      audience.sendMessage(
          Component.text(round + ". ", NamedTextColor.GOLD).append(roundDescription.roundInfo()));
      round++;
    }
  }
}
