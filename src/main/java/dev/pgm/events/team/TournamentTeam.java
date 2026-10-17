package dev.pgm.events.team;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tc.oc.pgm.util.Audience;

public interface TournamentTeam {

  String getName();

  List<? extends TournamentPlayer> getPlayers();

  default boolean containsPlayer(UUID player) {
    return getPlayers().stream().anyMatch(x -> x.getUUID().equals(player));
  }

  default void sendMessage(String message) {
    sendMessage(Component.text(message));
  }

  default void sendMessage(Component component) {
    forEachPlayer(p -> Audience.get(p).sendMessage(component));
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
