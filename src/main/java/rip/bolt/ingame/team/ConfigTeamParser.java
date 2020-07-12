package rip.bolt.ingame.team;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import rip.bolt.ingame.Tournament;

public class ConfigTeamParser {

    private List<TournamentTeam> teams;

    private static ConfigTeamParser instance;

    private ConfigTeamParser() {
        teams = parseTournamentTeams(new File(Tournament.get().getDataFolder(), "teams"));
    }

    private static List<TournamentTeam> parseTournamentTeams(File teamsFolder) {
        if (!teamsFolder.exists())
            teamsFolder.mkdirs();

        List<TournamentTeam> teamList = new ArrayList<TournamentTeam>();
        for (File child : teamsFolder.listFiles((file) -> file.getName().toLowerCase().endsWith(".yml"))) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(child);
                String teamName = config.getString("name");
                List<TournamentPlayer> players = config.getStringList("players").stream().map(String::trim).map(UUID::fromString).map(x -> TournamentPlayer.create(x, true)).collect(Collectors.toList());

                teamList.add(TournamentTeam.create(teamName, players));
        }

        return teamList;
    }

    public TournamentTeam getTeam(String name) {
        for (TournamentTeam team : teams)
            if (team.getName().equalsIgnoreCase(name))
                return team;

        return null;
    }

    public List<TournamentTeam> getTeams() {
        return teams;
    }

    public static ConfigTeamParser getInstance() {
        if (instance == null)
            instance = new ConfigTeamParser();

        return instance;
    }

}
