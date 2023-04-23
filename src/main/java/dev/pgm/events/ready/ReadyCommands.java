package dev.pgm.events.ready;

import dev.pgm.events.commands.CommandException;
import dev.pgm.events.utils.Response;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.cloud.commandframework.annotations.CommandDescription;
import tc.oc.pgm.lib.cloud.commandframework.annotations.CommandMethod;

public class ReadyCommands {

  private final ReadyManager manager;

  public ReadyCommands(ReadyManager readyManager) {
    this.manager = readyManager;
  }

  @CommandMethod("ready")
  @CommandDescription("Ready up")
  public void readyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canReady(player);
    if (response.isDenied()) throw new CommandException(response.getComponent());

    manager.ready(player.getParty(), player);
  }

  @CommandMethod("unready")
  @CommandDescription("Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canUnready(player);
    if (response.isDenied()) throw new CommandException(response.getComponent());

    manager.unready(player.getParty(), player);
  }
}
