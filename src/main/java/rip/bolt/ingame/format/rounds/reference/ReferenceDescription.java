package rip.bolt.ingame.format.rounds.reference;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import rip.bolt.ingame.format.rounds.RoundDescription;

public class ReferenceDescription implements RoundDescription {

    private final ReferenceRound referenceRound;

    public ReferenceDescription(ReferenceRound referenceRound) {
        this.referenceRound = referenceRound;
    }

    @Override
    public BaseComponent roundInfo() {
        return new TextComponent("Reference round -> referencing: " + referenceRound.settings().targetID());
    }

    @Override
    public String roundStatus() {
        return "reference round";
    }
}
