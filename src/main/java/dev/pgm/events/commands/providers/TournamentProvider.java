package dev.pgm.events.commands.providers;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.bukkit.parametric.provider.BukkitProvider;
import app.ashcon.intake.parametric.ProvisionException;
import dev.pgm.events.TournamentManager;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.format.FormatRound;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import org.bukkit.command.CommandSender;

public class TournamentProvider implements BukkitProvider<TournamentFormat> {

  private final TournamentManager tournamentManager;

  public TournamentProvider(TournamentManager tournamentManager) {
    this.tournamentManager = tournamentManager;
  }

  @Override
  public boolean isProvided() {
    return true;
  }

  @Override
  public TournamentFormat get(
      CommandSender commandSender, CommandArgs commandArgs, List<? extends Annotation> list)
      throws ArgumentException, ProvisionException {
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

    throw new ArgumentException("No tournament is currently running!");
  }
}
