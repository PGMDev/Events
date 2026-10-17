package dev.pgm.events.format.score;

import net.kyori.adventure.text.Component;

public interface FormattedScore {

  Component topLine();

  Component bottomLine();

  Component condensed();
}
