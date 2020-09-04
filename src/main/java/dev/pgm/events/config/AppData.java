package dev.pgm.events.config;

import dev.pgm.events.Tournament;

public class AppData {

  public static boolean observersMustReady() {
    return Tournament.get().getConfig().getBoolean("observers-must-ready");
  }
}
