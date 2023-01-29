package dev.pgm.events.commands;

import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.rounds.veto.VetoRound;
import dev.pgm.events.team.TournamentTeam;
import dev.pgm.events.team.TournamentTeamManager;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.lib.cloud.commandframework.annotations.Argument;
import tc.oc.pgm.lib.cloud.commandframework.annotations.CommandDescription;
import tc.oc.pgm.lib.cloud.commandframework.annotations.CommandMethod;

public class VetoCommands {

  @CommandMethod("veto <map>")
  @CommandDescription("Veto a map")
  public void veto(
      CommandSender sender,
      Match match,
      TournamentTeamManager teamManager,
      TournamentFormat format,
      @Argument("map") Integer option) {
    if (format.currentRound() == null || !(format.currentRound() instanceof VetoRound))
      throw new CommandException("Veto round is not currently running!");

    if (!(sender instanceof Player))
      throw new CommandException("Only players can run this command!");

    Player player = (Player) sender;
    Optional<TournamentTeam> team = teamManager.tournamentTeamPlayer((player).getUniqueId());
    if (!team.isPresent())
      throw new CommandException("Only players on teams can run this command!");

    if (!team.get().canVeto(player))
      throw new CommandException("You are not registered as a vetoer for this team!");

    try {
      int num = option - 1;
      VetoRound vetoRound = (VetoRound) format.currentRound();
      if (!vetoRound.validVetoNumber(num))
        throw new CommandException("That is not a valid veto number: " + (num + 1));

      if (!vetoRound.picking().equals(team))
        throw new CommandException("It isn't your turn to veto!");

      vetoRound.veto(match, team.get(), num);
    } catch (NumberFormatException e) {
      throw new CommandException("Invalid argument! Only takes numbers!");
    }
  }
}
