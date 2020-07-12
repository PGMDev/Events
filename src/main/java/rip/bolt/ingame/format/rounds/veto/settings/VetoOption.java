package rip.bolt.ingame.format.rounds.veto.settings;

import java.util.List;

import rip.bolt.ingame.format.rounds.RoundSettings;

public class VetoOption {

    private final List<RoundSettings> rounds;
    private final String name;

    public VetoOption(List<RoundSettings> rounds, String name) {
        this.rounds = rounds;
        this.name = name;
    }

    public List<RoundSettings> rounds() {
        return rounds;
    }

    public String name() {
        return name;
    }
}
