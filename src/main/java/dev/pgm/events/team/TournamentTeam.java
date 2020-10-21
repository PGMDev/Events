package dev.pgm.events.team;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface TournamentTeam {

  String getName();

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

  static TournamentTeam create(String name, List<TournamentPlayer> players) {
    return new DefaultTournamentTeam(name, players);
  }
}
