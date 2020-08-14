package rip.bolt.ingame.commands.providers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;

import rip.bolt.ingame.TournamentManager;
import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.format.FormatRound;
import rip.bolt.ingame.xml.MapFormatXMLParser;
import tc.oc.pgm.lib.app.ashcon.intake.argument.ArgumentException;
import tc.oc.pgm.lib.app.ashcon.intake.argument.CommandArgs;
import tc.oc.pgm.lib.app.ashcon.intake.argument.Namespace;
import tc.oc.pgm.lib.app.ashcon.intake.bukkit.parametric.provider.BukkitProvider;
import tc.oc.pgm.lib.app.ashcon.intake.parametric.ProvisionException;

public class TournamentProvider implements BukkitProvider<TournamentFormat> {

    private final TournamentManager tournamentManager;

    public TournamentProvider(TournamentManager tournamentManager) {
        this.tournamentManager = tournamentManager;
    }

    @Override
    public String getName() {
        return "Tournament format";
    }

    @Override
    public TournamentFormat get(CommandSender commandSender, CommandArgs commandArgs, List<? extends Annotation> list) throws ArgumentException, ProvisionException {
        String input = commandArgs.next();
        if(input.length()> 0){
            TournamentFormat tournamentFormat = MapFormatXMLParser.parse(input);
            if(tournamentFormat != null){
                return tournamentFormat;
            }

            throw new ArgumentException("No format found with name " + input + "!");
        }
        else {
            Optional<TournamentFormat> tournamentFormat = tournamentManager.currentTournament();

            if (tournamentFormat.isPresent()) {
                TournamentFormat format = tournamentFormat.get();
                if (format.currentRound() == null)
                    return format;

                if (format.currentRound() instanceof FormatRound)
                    format = ((FormatRound) format.currentRound()).formatTournament();

                if (format == null)
                    format = tournamentFormat.get(); // FormatTournamentImpl = null after round ends

                return format;
            }

            throw new ArgumentException("No tournament is currently running!");
        }
    }

    @Override
    public List<String> getSuggestions(
            String prefix,
            CommandSender sender,
            Namespace namespace,
            List<? extends Annotation> modifiers) {
        return MapFormatXMLParser.getFilesFromFolder();
    }
}
