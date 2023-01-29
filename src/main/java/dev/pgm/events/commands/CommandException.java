package dev.pgm.events.commands;

import static net.kyori.adventure.text.Component.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.ComponentMessageThrowable;
import org.jetbrains.annotations.Nullable;

public class CommandException extends RuntimeException implements ComponentMessageThrowable {

  private Component component;

  public CommandException(String message) {
    super(message);
  }

  public CommandException(Component component) {
    this.component = component;
  }

  @Override
  public @Nullable Component componentMessage() {
    if (component != null) return component;
    return text(this.getMessage());
  }
}
