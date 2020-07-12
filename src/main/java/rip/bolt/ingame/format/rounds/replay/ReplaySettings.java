package rip.bolt.ingame.format.rounds.replay;

import java.util.List;
import java.util.UUID;

import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.RoundSettings;
import rip.bolt.ingame.format.rounds.TournamentRound;
import rip.bolt.ingame.format.rounds.reference.ReferenceRoundSettings;

public class ReplaySettings extends RoundSettings {

    private final String futureID;
    private final List<ReferenceRoundSettings> referenceSettings;

    public ReplaySettings(String id, List<ReferenceRoundSettings> referenceSettings) {
        super(UUID.randomUUID().toString(), false, false);
        this.futureID = id;
        this.referenceSettings = referenceSettings;
    }

    public String futureID() {
        return futureID;
    }

    public List<ReferenceRoundSettings> referenceSettings() {
        return referenceSettings;
    }

    @Override
    public TournamentRound newRound(TournamentFormat format) {
        return new ReplayRound(format, this);
    }

    @Override
    public TournamentRound newRound(TournamentFormat format, String id) {
        return new ReplaySettings(id, referenceSettings).newRound(format);
    }
}
