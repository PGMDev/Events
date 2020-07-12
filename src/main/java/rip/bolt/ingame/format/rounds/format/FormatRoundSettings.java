package rip.bolt.ingame.format.rounds.format;

import java.util.ArrayList;
import java.util.List;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.RoundSettings;
import rip.bolt.ingame.format.rounds.TournamentRound;

public class FormatRoundSettings extends RoundSettings {

    private String name;
    private List<RoundSettings> rounds;
    private int bestOf;

    public FormatRoundSettings(String id, String name, List<RoundSettings> rounds, int bestOf) {
        super(id, true, false);
        this.name = name;
        this.rounds = rounds;
        this.bestOf = bestOf;
    }

    public String name() {
        return name;
    }

    public List<RoundSettings> roundSettings() {
        return rounds;
    }

    public int bestOf() {
        return bestOf;
    }

    @Override
    public TournamentRound newRound(TournamentFormat format) {
        return new FormatRound(format, this);
    }

    @Override
    public TournamentRound newRound(TournamentFormat format, String id) {
        return new FormatRoundSettings(id, name, new ArrayList<RoundSettings>(rounds), bestOf).newRound(format);
    }

}
