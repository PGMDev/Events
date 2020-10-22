package dev.pgm.events.team;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** Represents a single participating team composed of {@link TournamentPlayer}s */
public interface TournamentTeam {

  /**
   * Get the name of this team
   *
   * @return the name of this team
   */
  String getName();

  /**
   * Get a list of all players on this team
   *
   */
   * @return a list of the players on this team
  List<? extends TournamentPlayer> getPlayers();

  default boolean containsPlayer(UUID player) {
    return getPlayers().stream().anyMatch(x -> x.getUUID().equals(player));
  }

  default void sendMessage(String message) {
    forEachPlayer(x -> x.sendMessage(message));
  }

  default void sendMessage(BaseComponent component) {
    forEachPlayer(x -> x.sendMessage(component));
  }

  default void sendMessage(BaseComponent... components) {
    forEachPlayer(x -> x.sendMessage(components));
  }

  default void forEachPlayer(Consumer<Player> func) {
    getPlayers().stream()
        .map(TournamentPlayer::getUUID)
        .map(Bukkit::getPlayer)
        .filter(Objects::nonNull)
        .forEach(func);
  }

  default boolean canVeto(UUID uuid) {
    return getPlayers().stream().anyMatch(x -> x.canVeto() && x.getUUID().equals(uuid));
  }

  default boolean canVeto(Player player) {
    return canVeto(player.getUniqueId());
  }

  /**
   * Create a new {@link TournamentTeam}, returns a {@link DefaultTournamentTeam} by default
   *
   * @param name the team name
   * @param players all team members
   * @return An instance of this, {@link DefaultTournamentTeam} by default.
   */
  static TournamentTeam create(String name, List<TournamentPlayer> players) {
    return new DefaultTournamentTeam(name, players);
  }
}
