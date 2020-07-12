package rip.bolt.ingame.format.rounds.vetoselector;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import rip.bolt.ingame.format.rounds.RoundDescription;

public class VetoSelectorDescription implements RoundDescription {

    private final VetoSelectorRound vetoSelectorRound;

    public VetoSelectorDescription(VetoSelectorRound vetoSelectorRound) {
        this.vetoSelectorRound = vetoSelectorRound;
    }

    @Override
    public BaseComponent roundInfo() {
        return new TextComponent("VetoSelector round -> veto picker: " + vetoSelectorRound.getSelectingTeam() == null ? null : vetoSelectorRound.getSelectingTeam().getName());
    }

    @Override
    public String roundStatus() {
        return "veto selector";
    }

}
