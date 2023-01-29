package dev.pgm.events.commands.providers;

import dev.pgm.events.TournamentManager;
import dev.pgm.events.commands.CommandException;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.format.FormatRound;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import tc.oc.pgm.lib.cloud.commandframework.annotations.AnnotationAccessor;
import tc.oc.pgm.lib.cloud.commandframework.annotations.injection.ParameterInjector;
import tc.oc.pgm.lib.cloud.commandframework.context.CommandContext;
import tc.oc.pgm.lib.cloud.commandframework.exceptions.CommandExecutionException;

public class TournamentProvider implements ParameterInjector<CommandSender, TournamentFormat> {

  private final TournamentManager tournamentManager;

  public TournamentProvider(TournamentManager tournamentManager) {
    this.tournamentManager = tournamentManager;
  }

  @Override
  public @Nullable TournamentFormat create(
      @NonNull CommandContext<CommandSender> context,
      @NonNull AnnotationAccessor annotationAccessor) {
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
