package dev.pgm.events.api.teams;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigTeams implements TournamentTeamFetcher {

  @Override
  public List<? extends TournamentTeam> getTeams() {
    return parseTournamentTeams(
        new File(EventsPlugin.get().getDataFolder(), "teams"),
        new File(EventsPlugin.get().getDataFolder(), "teams.yml"));
  }

  private static List<TournamentTeam> parseTournamentTeams(File teamsFolder, File teamsFile) {
    if (!teamsFolder.exists()) teamsFolder.mkdirs();

    List<TournamentTeam> teamList = new ArrayList<TournamentTeam>();
    for (File child :
        teamsFolder.listFiles((file) -> file.getName().toLowerCase().endsWith(".yml"))) {
      FileConfiguration config = YamlConfiguration.loadConfiguration(child);
      String teamName = config.getString("name");
      List<TournamentPlayer> players =
          config.getStringList("players").stream()
              .map(String::trim)
              .map(UUID::fromString)
              .map(x -> TournamentPlayer.create(x, true))
              .collect(Collectors.toList());

      teamList.add(TournamentTeam.create(teamName, players));
    }

    if (teamsFile.exists()) {
      YamlConfiguration teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
      for (Object object : teamsConfig.getList("teams")) {
        if (!(object instanceof Map<?, ?>)) {
          System.out.println(
              "Invalid type in teams.yml ("
                  + object.getClass().getName()
                  + ": "
                  + object.toString()
                  + ")! Skipping...");
          continue;
        }

        Map<Object, Object> team = (Map<Object, Object>) object;
        String teamName = (String) team.get("name");
        List<TournamentPlayer> players =
            ((List<String>) team.get("players"))
                .stream()
                    .map(String::trim)
                    .map(UUID::fromString)
                    .map(x -> TournamentPlayer.create(x, true))
                    .collect(Collectors.toList());

        teamList.add(TournamentTeam.create(teamName, players));
      }
    }

    return teamList;
  }
}
