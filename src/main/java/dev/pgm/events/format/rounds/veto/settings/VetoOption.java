package dev.pgm.events.format.rounds.veto.settings;

import dev.pgm.events.format.rounds.RoundSettings;
import java.util.List;

public record VetoOption(List<RoundSettings> rounds, String name) {}
