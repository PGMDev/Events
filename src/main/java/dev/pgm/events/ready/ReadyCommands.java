package dev.pgm.events.ready;

import dev.pgm.events.commands.CommandException;
import dev.pgm.events.utils.Response;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.Command;
import tc.oc.pgm.lib.org.incendo.cloud.annotations.CommandDescription;

public class ReadyCommands {

  private final ReadyManager manager;

  public ReadyCommands(ReadyManager readyManager) {
    this.manager = readyManager;
  }

  @Command("ready")
  @CommandDescription("Ready up")
  public void readyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canReady(player);
    if (response.isDenied()) throw new CommandException(response.getComponent());

    manager.ready(player.getParty(), player);
  }

  @Command("unready")
  @CommandDescription("Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canUnready(player);
    if (response.isDenied()) throw new CommandException(response.getComponent());

    manager.unready(player.getParty(), player);
  }
}
