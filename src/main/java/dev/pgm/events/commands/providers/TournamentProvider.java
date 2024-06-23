package dev.pgm.events.commands.providers;

import dev.pgm.events.TournamentManager;
import dev.pgm.events.commands.CommandException;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.format.FormatRound;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.lib.org.incendo.cloud.context.CommandContext;
import tc.oc.pgm.lib.org.incendo.cloud.exception.CommandExecutionException;
import tc.oc.pgm.lib.org.incendo.cloud.injection.ParameterInjector;
import tc.oc.pgm.lib.org.incendo.cloud.util.annotation.AnnotationAccessor;

public class TournamentProvider implements ParameterInjector<CommandSender, TournamentFormat> {

  private final TournamentManager tournamentManager;

  public TournamentProvider(TournamentManager tournamentManager) {
    this.tournamentManager = tournamentManager;
  }

  @Override
  public @Nullable TournamentFormat create(
      @NotNull CommandContext<CommandSender> context,
      @NotNull AnnotationAccessor annotationAccessor) {
    Optional<TournamentFormat> tournamentFormat = tournamentManager.currentTournament();
    if (tournamentFormat.isPresent()) {
      TournamentFormat format = tournamentFormat.get();
      if (format.currentRound() == null) return format;

      if (format.currentRound() instanceof FormatRound)
        format = ((FormatRound) format.currentRound()).formatTournament();

      if (format == null)
        format = tournamentFormat.get(); // FormatTournamentImpl = null after round ends

      return format;
    }

    throw new CommandExecutionException(
        new CommandException("No tournament is currently running!"));
  }
}
