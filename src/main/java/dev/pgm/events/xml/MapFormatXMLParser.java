package dev.pgm.events.xml;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.format.RoundReferenceHolder;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.TournamentFormatImpl;
import dev.pgm.events.format.TournamentRoundOptions;
import dev.pgm.events.format.winner.BestOfCalculation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.jspecify.annotations.NonNull;
import tc.oc.pgm.lib.org.jdom2.Document;
import tc.oc.pgm.lib.org.jdom2.Element;
import tc.oc.pgm.lib.org.jdom2.JDOMException;
import tc.oc.pgm.lib.org.jdom2.input.JDOMParseException;
import tc.oc.pgm.lib.org.jdom2.input.SAXBuilder;

public class MapFormatXMLParser {

  public static TournamentFormat parse(String name) {
    // messages are shown to the sender, so they must not include server paths
    Path formats =
        EventsPlugin.get().getDataFolder().toPath().resolve("formats").normalize();
    Path file = formats.resolve(name + ".xml").normalize();
    if (!file.startsWith(formats) || !Files.isRegularFile(file))
      throw new IllegalArgumentException("Format " + name + " was not found!");

    Document document;
    try {
      document = new SAXBuilder().build(file.toFile());
    } catch (JDOMParseException e) {
      throw new IllegalArgumentException(
          "Format " + name + " has invalid XML on line " + e.getLineNumber() + "!", e);
    } catch (JDOMException | IOException e) {
      throw new IllegalArgumentException("Format " + name + " could not be loaded!", e);
    }
    return parse(document.getRootElement());
  }

  public static TournamentFormat parse(Element root) {
    TournamentFormat format = getTournamentFormat(RoundParser.parseInt(root, "best-of", null));

    if (!root.getName().equalsIgnoreCase("format"))
      System.out.println(
          "Expecting root element to be format. Got " + root.getName() + " instead!");

    for (Element round : root.getChildren()) format.addRound(RoundParser.parse(format, round));

    return format;
  }

  private static @NonNull TournamentFormat getTournamentFormat(int bestOf) {
    TournamentRoundOptions options = new TournamentRoundOptions(
        false,
        true,
        true,
        Duration.ofSeconds(20),
        Duration.ofSeconds(30),
        Duration.ofSeconds(40),
        new BestOfCalculation<>(bestOf));
    return new TournamentFormatImpl(
        EventsPlugin.get().getTeamManager(), options, new RoundReferenceHolder());
  }
}
