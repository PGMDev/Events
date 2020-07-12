package rip.bolt.ingame.format.rounds.vetoselector;

import java.util.UUID;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.RoundSettings;
import rip.bolt.ingame.format.rounds.TournamentRound;

public class VetoSelectorSettings extends RoundSettings {

    public VetoSelectorSettings() {
        this(UUID.randomUUID().toString());
    }

    public VetoSelectorSettings(String id) {
        super(id, true, false);
    }

    @Override
    public TournamentRound newRound(TournamentFormat format) {
        return new VetoSelectorRound(format, this);
    }

    @Override
    public TournamentRound newRound(TournamentFormat format, String id) {
        return new VetoSelectorSettings(id).newRound(format);
    }

}
