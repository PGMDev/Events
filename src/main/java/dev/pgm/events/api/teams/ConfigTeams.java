package dev.pgm.events.api.teams;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.team.TournamentPlayer;
import dev.pgm.events.team.TournamentTeam;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
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
    Logger logger = EventsPlugin.get().getLogger();
    if (!teamsFolder.exists() && !teamsFolder.mkdirs()) {
      logger.warning("Failed to create teams folder: " + teamsFolder.getPath());
    }

    List<TournamentTeam> teamList = new ArrayList<>();

    File[] teamFiles = teamsFolder.listFiles(f -> f.getName().toLowerCase().endsWith(".yml"));
    if (teamFiles != null) {
      for (File child : teamFiles) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(child);
        String teamName = config.getString("name");
        List<TournamentPlayer> players = parsePlayers(config.getStringList("players"), logger);
        teamList.add(TournamentTeam.create(teamName, players));
      }
    }

    if (teamsFile.exists()) {
      YamlConfiguration teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
      for (Object object : teamsConfig.getList("teams")) {
        if (!(object instanceof Map<?, ?> rawTeam)) {
          logger.warning("Invalid type in teams.yml ("
              + object.getClass().getName()
              + ": "
              + object
              + ")! Skipping...");
          continue;
        }

        Object nameObj = rawTeam.get("name");
        Object playersObj = rawTeam.get("players");

        if (!(nameObj instanceof String teamName)) {
          logger.warning("Team entry in teams.yml is missing a valid 'name'. Skipping...");
          continue;
        }

        if (!(playersObj instanceof List<?> rawPlayers)) {
          logger.warning(
              "Team '" + nameObj + "' in teams.yml is missing a valid 'players' list. Skipping...");
          continue;
        }

        List<String> playerStrings = new ArrayList<>();
        for (Object p : rawPlayers) {
          if (p instanceof String s) {
            playerStrings.add(s);
          } else {
            logger.warning(
                "Non-string player entry in team '" + nameObj + "': " + p + ". Skipping entry...");
          }
        }

        teamList.add(TournamentTeam.create(teamName, parsePlayers(playerStrings, logger)));
      }
    }

    return teamList;
  }

  private static List<TournamentPlayer> parsePlayers(List<String> uuids, Logger logger) {
    List<TournamentPlayer> players = new ArrayList<>();
    for (String raw : uuids) {
      try {
        players.add(TournamentPlayer.create(UUID.fromString(raw.trim()), true));
      } catch (IllegalArgumentException e) {
        logger.warning("Invalid UUID '" + raw + "' in team config. Skipping...");
      }
    }
    return players;
  }
}
