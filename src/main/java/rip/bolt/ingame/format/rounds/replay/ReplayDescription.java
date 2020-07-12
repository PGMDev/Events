package rip.bolt.ingame.format.rounds.replay;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import rip.bolt.ingame.format.rounds.RoundDescription;
import rip.bolt.ingame.format.rounds.RoundPhase;

public class ReplayDescription implements RoundDescription {

    private final ReplayRound round;

    public ReplayDescription(ReplayRound round) {
        this.round = round;
    }

    @Override
    public BaseComponent roundInfo() {
        if (round.phase() != RoundPhase.FINISHED) // haven't yet decided if we're gonna play if here
            return new TextComponent(ChatColor.GRAY + "Replay tied round");

        if (round.shouldShowInHistory()) // not gonna replay
            return new TextComponent("" + ChatColor.GRAY + ChatColor.STRIKETHROUGH + "Not replaying a round");

        return new TextComponent("Error replaying a round");
    }

    @Override
    public String roundStatus() {
        return null;
    }

}
