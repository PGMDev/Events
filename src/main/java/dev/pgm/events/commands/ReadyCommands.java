package dev.pgm.events.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import dev.pgm.events.ready.ReadyManager;
import dev.pgm.events.utils.Response;
import org.bukkit.command.CommandSender;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.util.Audience;

public class ReadyCommands extends BaseCommand {

  @Dependency private ReadyManager manager;

  @CommandAlias("ready")
  @Description("Ready up")
  public void readyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canReady(player);

    if (response.isDenied()) {
      Audience.get(sender).sendWarning(response.getMessage());
      return;
    }

    manager.ready(player.getParty(), player);
  }

  @CommandAlias("unready")
  @Description("Mark your team as no longer being ready")
  public void unreadyCommand(CommandSender sender, MatchPlayer player) {
    Response response = manager.canUnready(player);

    if (response.isDenied()) {
      Audience.get(sender).sendWarning(response.getMessage());
      return;
    }

    manager.unready(player.getParty(), player);
  }
}
