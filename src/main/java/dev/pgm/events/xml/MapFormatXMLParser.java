package dev.pgm.events.xml;

import dev.pgm.events.EventsPlugin;
import dev.pgm.events.format.RoundReferenceHolder;
import dev.pgm.events.format.TournamentFormat;
import dev.pgm.events.format.TournamentFormatImpl;
import dev.pgm.events.format.TournamentRoundOptions;
import dev.pgm.events.format.winner.BestOfCalculation;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.jspecify.annotations.NonNull;
import tc.oc.pgm.lib.org.jdom2.Document;
import tc.oc.pgm.lib.org.jdom2.Element;
import tc.oc.pgm.lib.org.jdom2.JDOMException;
import tc.oc.pgm.lib.org.jdom2.input.SAXBuilder;

public class MapFormatXMLParser {

  public static TournamentFormat parse(String name) {
    File poolsFolder = new File(EventsPlugin.get().getDataFolder(), "formats");
    File xmlFile = new File(poolsFolder, name + ".xml");
    Document document;
    try {
      document = new SAXBuilder().build(xmlFile);
      Element root = document.getRootElement();
      return parse(root);
    } catch (JDOMException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static TournamentFormat parse(Element root) {
    String bestOfArgs = root.getAttributeValue("best-of");
    TournamentFormat format = getTournamentFormat(bestOfArgs);

    if (!root.getName().equalsIgnoreCase("format"))
      System.out.println(
          "Expecting root element to be format. Got " + root.getName() + " instead!");

    for (Element round : root.getChildren()) format.addRound(RoundParser.parse(format, round));

    return format;
  }

  private static @NonNull TournamentFormat getTournamentFormat(String bestOfArgs) {
    if (bestOfArgs == null) throw new IllegalArgumentException("No best-of specified on format!");
    int bestOf = Integer.parseInt(bestOfArgs);

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
