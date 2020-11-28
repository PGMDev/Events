package dev.pgm.events.config;

import dev.pgm.events.EventsPlugin;

public class AppData {

  public static boolean observersMustReady() {
    return EventsPlugin.get().getConfig().getBoolean("observers-must-ready");
  }
}
