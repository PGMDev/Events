package dev.pgm.events.config;

/**
 * Any method that takes a {@link Context} should also explain what giving any other context than
 * {@link Context#NORMAL} will return.
 *
 * @see dev.pgm.events.format.Tournament#roundsInformation(Context)
 */
public enum Context {
  NORMAL,
  DEBUG
}
