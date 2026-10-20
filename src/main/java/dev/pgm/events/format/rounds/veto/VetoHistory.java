package dev.pgm.events.format.rounds.veto;

import dev.pgm.events.format.rounds.veto.settings.VetoOption;
import dev.pgm.events.format.rounds.veto.settings.VetoSettings;
import dev.pgm.events.team.TournamentTeam;
import org.jspecify.annotations.Nullable;

public record VetoHistory(
    @Nullable TournamentTeam team,
    VetoSettings.VetoType vetoType,
    VetoOption optionChosen,
    boolean shouldAnnounce) {}
