package rip.bolt.ingame.team;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.md_5.bungee.api.chat.BaseComponent;
import rip.bolt.ingame.api.definitions.Team;

@JsonDeserialize(as = Team.class)
public interface TournamentTeam {

    public String getName();

    public List<TournamentPlayer> getPlayers();

    public default boolean containsPlayer(UUID player) {
        return getPlayers().stream().anyMatch(x -> x.getUUID().equals(player));
    }

    public default void sendMessage(String message) {
        forEachPlayer(x -> x.sendMessage(message));
    }

    public default void sendMessage(BaseComponent component) {
        forEachPlayer(x -> x.sendMessage(component));
    }

    public default void sendMessage(BaseComponent... components) {
        forEachPlayer(x -> x.sendMessage(components));
    }

    public default void forEachPlayer(Consumer<Player> func) {
        getPlayers().stream().map(TournamentPlayer::getUUID).map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(func);
    }

    public default boolean canVeto(UUID uuid) {
        //return players.stream().anyMatch(x -> x.canVeto() && x.id().equals(uuid));
        return true;
    }

    public default boolean canVeto(Player player) {
        //return canVeto(player.getUniqueId());
        return true;
    }

    public void addPlayer(TournamentPlayer player);

    public void removePlayer(UUID uuid);

    public static TournamentTeam create(String name, List<TournamentPlayer> players) {
        return new Team(name, players);
    }

}
