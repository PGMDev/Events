package dev.pgm.events.listeners;

import static net.kyori.adventure.text.Component.text;

import dev.pgm.events.config.AppData;
import dev.pgm.events.team.TournamentTeamManager;
import dev.pgm.events.utils.JoinUtils;
import dev.pgm.events.utils.Response;
import java.util.Optional;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.integration.Integration;
import tc.oc.pgm.api.player.event.MatchPlayerAddEvent;
import tc.oc.pgm.events.PlayerParticipationStartEvent;
import tc.oc.pgm.events.PlayerParticipationStopEvent;
import tc.oc.pgm.teams.Team;

public class PlayerJoinListen implements Listener {

  private final TournamentTeamManager manager;

  public PlayerJoinListen(TournamentTeamManager manager) {
    this.manager = manager;
  }

  @EventHandler
  public void onJoin(MatchPlayerAddEvent event) {
    Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getId());
    if (playerTeam.isPresent()) {
      Team team = playerTeam.get();
      Response response = JoinUtils.canJoin(event.getPlayer().getId(), team);
      if (response.isAllowed()) {
        event.setInitialParty(team);
      } else if (!AppData.allowSpectators()) {
        // team is full, lets kick this man
        // maybe delay this? -- was having issues when it wasn't delayed. Feels hacky but ok for now
        Bukkit.getScheduler()
            .runTaskLater(
                PGM.get(),
                () -> Bukkit.getPlayer(event.getPlayer().getId()).kickPlayer(response.getMessage()),
                2);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void beforeLogin(PlayerLoginEvent event) {
    Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getUniqueId());
    boolean allowSpectators = AppData.allowSpectators();

    // check if the player is on one of the teams
    if (playerTeam.isPresent()) {
      Team team = playerTeam.get();

      Response response = JoinUtils.canJoin(event.getPlayer().getUniqueId(), team);

      if (response.isAllowed()) {
        event.allow();
      } else if (!allowSpectators) {
        // team is full of blitz not allowed to spectate -- lets kick this mad lad
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, response.getMessage());
      }
      return;
    }

    if (allowSpectators
        || event.getPlayer().hasPermission("events.spectate")
        || event.getPlayer().hasPermission("events.spectate.vanish")) return;

    // not on a team and no spectate permission
    event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
    event.setKickMessage("You're not allowed to spectate this match!");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void vanish(PlayerJoinEvent event) {
    if (!event.getPlayer().isOp()
        && event.getPlayer().hasPermission("events.spectate.vanish")
        && !manager.playerTeam(event.getPlayer().getUniqueId()).isPresent())
      Integration.setVanished(PGM.get().getMatchManager().getPlayer(event.getPlayer()), true, true);
  }

  @EventHandler
  public void onParticipate(PlayerParticipationStartEvent event) {
    Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getId());
    // check if the player is on one of the teams
    if (playerTeam.isPresent()) {
      Team team = playerTeam.get();
      if (!JoinUtils.isPartyFull(team)) return;
    }

    event.cancel(text("You may not join in a tournament setting!", NamedTextColor.RED));
  }

  @EventHandler
  public void onLeaveParticipate(PlayerParticipationStopEvent event) {
    Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getId());
    // check if the player is on one of the teams

    if (playerTeam.isPresent() && playerTeam.get().equals(event.getCompetitor()))
      event.cancel(text("You may not leave in a tournament setting!", NamedTextColor.RED));

    if (JoinUtils.isBlitzEliminated(event.getPlayer())) {
      event.setCancelled(false);
    }
  }
}
