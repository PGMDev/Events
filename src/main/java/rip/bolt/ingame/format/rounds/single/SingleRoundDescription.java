package rip.bolt.ingame.format.rounds.single;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import rip.bolt.ingame.format.TournamentFormat;
import rip.bolt.ingame.format.rounds.RoundDescription;
import rip.bolt.ingame.team.TournamentTeam;

import java.util.Collection;
import java.util.stream.Collectors;

public class SingleRoundDescription implements RoundDescription {

    private final String mapName;
    private final SingleRound singleRound;
    private final TournamentFormat format;

    private final String defString;

    public SingleRoundDescription(String mapName, SingleRound singleRound, TournamentFormat format) {
        this.mapName = mapName;
        this.singleRound = singleRound;
        this.format = format;
        this.defString = ChatColor.GRAY + "Match on " + ChatColor.GOLD + this.mapName + ChatColor.AQUA;
    }

    @Override
    public BaseComponent roundInfo() {
        switch (singleRound.phase()) {
            case UNLOADED:
                return new TextComponent(defString);
            case WAITING:
                return new TextComponent(defString + " - " + ChatColor.GRAY + "Waiting");
            case RUNNING:
                return new TextComponent(defString + " - " + ChatColor.GREEN + "Running");
            case FINISHED:
                return new TextComponent(defString + " - " + winnersString());
        }
        return new TextComponent("NULL");
    }

    private String winnersString() {
        Collection<? extends TournamentTeam> teams = singleRound.scores().keySet();
        if (teams.size() == 0) {
            //draw
            return drawString();
        }

        String teamString = teams.stream()
                .map(x -> format.teamManager().formattedName(x))
                .collect(Collectors.joining(ChatColor.GRAY + ", "));

        teamString += " won";
        return teamString;
    }

    private String drawString() {
        return ChatColor.GRAY + "Draw";
    }

    @Override
    public String roundStatus() {
        return null;
    }
}
