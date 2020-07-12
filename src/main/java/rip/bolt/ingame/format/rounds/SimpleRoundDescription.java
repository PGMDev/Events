package rip.bolt.ingame.format.rounds;

import net.md_5.bungee.api.chat.BaseComponent;

public class SimpleRoundDescription implements RoundDescription {

    private final String status;
    private final BaseComponent roundInfo;

    public SimpleRoundDescription(String status, BaseComponent roundInfo) {
        this.status = status;
        this.roundInfo = roundInfo;
    }

    @Override
    public BaseComponent roundInfo() {
        return roundInfo;
    }

    @Override
    public String roundStatus() {
        return status;
    }

}
