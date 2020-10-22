package dev.pgm.events.format.score;

/** Formats a score overview for a round */
public interface FormattedScore {

  /** The top line of a title to display the winner */
  String topLine();

  /** The bottom lime of a title to display the winner */
  String bottomLine();

  /** A condensed form of the score, fit for sending as a chat message */
  String condensed();
}
