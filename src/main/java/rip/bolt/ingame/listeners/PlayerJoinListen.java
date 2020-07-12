package rip.bolt.ingame.listeners;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import net.md_5.bungee.api.ChatColor;
import rip.bolt.ingame.team.TournamentTeamManager;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.player.event.MatchPlayerAddEvent;
import tc.oc.pgm.blitz.BlitzMatchModule;
import tc.oc.pgm.events.PlayerParticipationStartEvent;
import tc.oc.pgm.events.PlayerParticipationStopEvent;
import tc.oc.pgm.lib.net.kyori.text.TextComponent;
import tc.oc.pgm.teams.Team;

public class PlayerJoinListen implements Listener {

    private final TournamentTeamManager manager;

    public PlayerJoinListen(TournamentTeamManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
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
                Bukkit.getScheduler().runTaskLater(PGM.get(), () -> Bukkit.getPlayer(event.getPlayer().getId()).kickPlayer("Your team is full!"), 2);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void beforeLogin(PlayerLoginEvent event) {
        Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getUniqueId());
        // check if the player is on one of the teams
        if (playerTeam.isPresent()) {
            Team team = playerTeam.get();
            if (!isFull(team))
                return;

            // team is full -- lets kick this mad lad
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Your team is full!");
            return;
        }

        if (event.getPlayer().hasPermission("ingame.spectate"))
            return;

        // not on a team and no spectate permission
        event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        event.setKickMessage("You're not allowed to spectate this match!");
    }

    @EventHandler
    public void onParticipate(PlayerParticipationStartEvent event) {
        Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getId());
        // check if the player is on one of the teams
        if (playerTeam.isPresent()) {
            Team team = playerTeam.get();
            if (!isFull(team))
                return;
        }

        event.cancel(TextComponent.of(ChatColor.RED + "You may not join in a tournament setting!"));
        // event.setCancelled(true);
    }

    @EventHandler
    public void onLeaveParticipate(PlayerParticipationStopEvent event) {
        Optional<Team> playerTeam = manager.playerTeam(event.getPlayer().getId());
        // check if the player is on one of the teams
        if (playerTeam.isPresent())
            event.cancel(TextComponent.of(ChatColor.RED + "You may not leave in a tournament setting!"));

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
