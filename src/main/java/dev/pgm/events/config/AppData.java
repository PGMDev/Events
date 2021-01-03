package dev.pgm.events.config;

import dev.pgm.events.Tournament;

/**
 * Config options used around the plugin.
 *
 * <p>Values are are stored in the <code>`resources/config.yml`</code> file.
 */
public class AppData {

  public static boolean observersMustReady() {
    return Tournament.get().getConfig().getBoolean("observers-must-ready", true);
  }

  public static boolean readyFullTeamRequired() {
    return Tournament.get().getConfig().getBoolean("ready-full-team-required", false);
  }

  public static boolean readyReminders() {
    return Tournament.get().getConfig().getBoolean("ready-reminders", true);
  }

  public static boolean autoUnready() {
    return Tournament.get().getConfig().getBoolean("auto-unready", false);
  }
}
