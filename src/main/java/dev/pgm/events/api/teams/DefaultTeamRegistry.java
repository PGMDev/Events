package dev.pgm.events.api.teams;

import com.google.common.collect.Maps;
import dev.pgm.events.EventsPlugin;
import dev.pgm.events.team.TournamentTeam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import tc.oc.pgm.util.StringUtils;

public class DefaultTeamRegistry implements TournamentTeamRegistry {

  private final TournamentTeamFetcher tournamentTeamFetcher;
  private final Map<String, TournamentTeam> teamMap;

  private DefaultTeamRegistry(TournamentTeamFetcher tournamentTeamFetcher) {
    this.tournamentTeamFetcher = tournamentTeamFetcher;
    this.teamMap = Maps.newHashMap();
  }

  public static TournamentTeamRegistry createRegistry(TournamentTeamFetcher tournamentTeamFetcher) {
    TournamentTeamRegistry registry = new DefaultTeamRegistry(tournamentTeamFetcher);
    registry.reload();
    return registry;
  }

  @Override
  public TournamentTeam findExact(String name) {
    if (teamMap.containsKey(name)) {
      return teamMap.get(name);
    }
    return null;
  }

  @Override
  public TournamentTeam getTeam(String name) {
    TournamentTeam found = findExact(name);
    return found != null ? found : StringUtils.bestFuzzyMatch(name, teamMap, 0.9);
  }

  @Override
  public List<? extends TournamentTeam> getTeams() {
    return new ArrayList<>(teamMap.values());
  }

  @Override
  public void reload() {
    Bukkit.getScheduler()
        .runTaskAsynchronously(
            EventsPlugin.get(),
            () -> {
              List<? extends TournamentTeam> teams = tournamentTeamFetcher.getTeams();
              Bukkit.getScheduler()
                  .runTask(
                      EventsPlugin.get(),
                      () -> {
                        teams.forEach(x -> teamMap.put(x.getName(), x));
                        Bukkit.getOnlinePlayers().stream()
                            .filter(x -> x.hasPermission("events.staff"))
                            .forEach(x -> x.sendMessage(ChatColor.AQUA + "Teams reloaded!"));
                      });
            });
  }
}
