package dev.pgm.events.format.shutdown;

import dev.pgm.events.utils.TimeFormatter;
import java.time.Duration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.countdowns.MatchCountdown;
import tc.oc.pgm.lib.net.kyori.text.Component;
import tc.oc.pgm.lib.net.kyori.text.TextComponent;
import tc.oc.pgm.util.chat.Sound;

public class ShutdownCountdown extends MatchCountdown {

  protected static final Sound COUNT_SOUND = new Sound("note.pling", 1f, 1.19f);

  public ShutdownCountdown(Match match) {
    super(match);
  }

  @Override
  public void onTick(Duration remaining, Duration total) {
    super.onTick(remaining, total);
    if (remaining.getSeconds() >= 1 && remaining.getSeconds() <= 3)
      getMatch().playSound(COUNT_SOUND);
  }

  @Override
  protected boolean showTitle() {
    return remaining.getSeconds() <= 3 || super.showTitle();
  }

  @Override
  protected Component formatText() {
    return TextComponent.of(
        ChatColor.AQUA + "Server will shut down in " + TimeFormatter.seconds(remaining));
  }

  @Override
  public void onEnd(Duration total) {
    super.onEnd(total);
    // shutdown server
    Bukkit.getServer().shutdown();
  }
}
