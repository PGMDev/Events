package dev.pgm.events.utils;

import tc.oc.pgm.lib.net.kyori.adventure.text.Component;
import tc.oc.pgm.lib.net.kyori.adventure.text.event.ClickEvent;
import tc.oc.pgm.lib.net.kyori.adventure.text.format.NamedTextColor;
import tc.oc.pgm.lib.net.kyori.adventure.text.format.Style;

public class Components {

  /**
   * Creates a formatted and clickable command component.
   *
   * @param style formatting of command
   * @param command command with or without `/`
   * @param cmdArgs command arguments
   * @return text component
   */
  public static Component command(Style style, String command, String... cmdArgs) {
    StringBuilder builder = new StringBuilder();
    if (!command.startsWith("/")) builder.append("/");
    builder.append(command);

    for (String arg : cmdArgs) builder.append(" ").append(Components.toArgument(arg));
    command = builder.toString();

    return Component.text(command, style)
        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, command))
        .hoverEvent(
            Component.text("Click to run ", NamedTextColor.GREEN)
                .append(Component.text(command, style)));
  }

  private static String toArgument(String input) {
    if (input == null) return null;
    return input.replace(" ", "┈");
  }
}
