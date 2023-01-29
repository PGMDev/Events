package dev.pgm.events.listeners;

import static net.kyori.adventure.text.Component.text;

import dev.pgm.events.team.TournamentTeamManager;
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
import tc.oc.pgm.blitz.BlitzMatchModule;
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
      if (!isFull(team)) {
        if (event.getMatch().isRunning() && event.getMatch().hasModule(BlitzMatchModule.class))
          return;

        event.setInitialParty(team);
      } else {
        // team is full, lets kick this man
        // maybe delay this? -- was having issues when it wasn't delayed. Feels hacky but ok for now
        Bukkit.getScheduler()
            .runTaskLater(
                PGM.get(),
                () -> Bukkit.getPlayer(event.getPlayer().getId()).kickPlayer("Your team is full!"),
                2);
      }
    }
  }

  @EventHandler(priority = EventPriority.LOW)
  public void beforeLogin(PlayerLoginEvent event) {
    Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getUniqueId());
    // check if the player is on one of the teams
    if (playerTeam.isPresent()) {
      Team team = playerTeam.get();

      if (isFull(team)) {
        // team is full -- lets kick this mad lad
        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your team is full!");
      } else {
        event.allow();
      }
      return;
    }

    if (event.getPlayer().hasPermission("events.spectate")
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
      if (!isFull(team)) return;
    }

    event.cancel(text("You may not join in a tournament setting!", NamedTextColor.RED));
  }

  @EventHandler
  public void onLeaveParticipate(PlayerParticipationStopEvent event) {
    Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getId());
    // check if the player is on one of the teams

    if (playerTeam.isPresent() && playerTeam.get().equals(event.getCompetitor()))
      event.cancel(text("You may not leave in a tournament setting!", NamedTextColor.RED));

    BlitzMatchModule blitz = event.getMatch().getModule(BlitzMatchModule.class);
    if (blitz != null) {
      if (blitz.getNumOfLives(event.getPlayer().getId()) <= 0) {
        event.setCancelled(false);
        return;
      }
    }
  }

  private boolean isFull(Team team) {
    return team.getSizeAfterJoin(null, team, false) >= team.getMaxPlayers();
  }
}
