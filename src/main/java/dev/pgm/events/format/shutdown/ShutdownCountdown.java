package dev.pgm.events.format.shutdown;

import static net.kyori.adventure.text.Component.text;

import dev.pgm.events.utils.TimeFormatter;
import java.time.Duration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.countdowns.MatchCountdown;
import tc.oc.pgm.util.bukkit.Sounds;

public class ShutdownCountdown extends MatchCountdown {
  public ShutdownCountdown(Match match) {
    super(match);
  }

  @Override
  public void onTick(Duration remaining, Duration total) {
    super.onTick(remaining, total);
    if (remaining.getSeconds() >= 1 && remaining.getSeconds() <= 3)
      getMatch().playSound(Sounds.MATCH_COUNTDOWN);
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
