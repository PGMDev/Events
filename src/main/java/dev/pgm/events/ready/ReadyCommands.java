package dev.pgm.events.ready;

import dev.pgm.events.utils.Response;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.app.ashcon.intake.Command;
import tc.oc.pgm.util.Audience;

public class ReadyCommands {

  private final ReadyManager manager;

  public ReadyCommands(ReadyManager readyManager) {
    this.manager = readyManager;
  }

  @Command(aliases = "ready", desc = "Ready up")
  public void readyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canReady(player);

    if (response.isDenied()) {
      Audience.get(sender).sendWarning(response.getMessage());
      return;
    }

    manager.ready(player.getParty());
  }

  @Command(aliases = "unready", desc = "Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canUnready(player);

    if (response.isDenied()) {
      Audience.get(sender).sendWarning(response.getMessage());
      return;
    }

    manager.unready(player.getParty());
  }
}
