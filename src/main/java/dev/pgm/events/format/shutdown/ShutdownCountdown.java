package dev.pgm.events.format.shutdown;

import static tc.oc.pgm.lib.net.kyori.adventure.key.Key.key;
import static tc.oc.pgm.lib.net.kyori.adventure.text.Component.text;

import dev.pgm.events.utils.TimeFormatter;
import java.time.Duration;
import org.bukkit.Bukkit;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.countdowns.MatchCountdown;
import tc.oc.pgm.lib.net.kyori.adventure.sound.Sound;
import tc.oc.pgm.lib.net.kyori.adventure.text.Component;
import tc.oc.pgm.lib.net.kyori.adventure.text.format.NamedTextColor;

public class ShutdownCountdown extends MatchCountdown {

  protected static final Sound COUNT_SOUND =
      Sound.sound(key("note.pling"), Sound.Source.MASTER, 1f, 1.19f);

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
    return text(
        "Server will shut down in " + TimeFormatter.seconds(remaining), NamedTextColor.AQUA);
  }

  @Override
  public void onEnd(Duration total) {
    super.onEnd(total);
    // shutdown server
    Bukkit.getServer().shutdown();
  }
}
