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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class MapFormatXMLParser {

  public static TournamentFormat parse(String name) {
    File poolsFolder = new File(EventsPlugin.get().getDataFolder(), "formats");
    File xmlFile = new File(poolsFolder, name + ".xml");
    Document document = null;
    try {
      document = new SAXBuilder().build(xmlFile);
      Element root = document.getRootElement();
      return parse(root);
    } catch (JDOMException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static TournamentFormat parse(Element root) throws JDOMException {
    String bestOfArgs = root.getAttributeValue("best-of");
    if (bestOfArgs == null) throw new IllegalArgumentException("No best-of specified on format!");
    int bestOf = Integer.parseInt(bestOfArgs);

    TournamentRoundOptions options =
        new TournamentRoundOptions(
            false,
            true,
            true,
            Duration.ofSeconds(20),
            Duration.ofSeconds(30),
            Duration.ofSeconds(40),
            new BestOfCalculation<>(bestOf));
    TournamentFormat format =
        new TournamentFormatImpl(
            EventsPlugin.get().getTeamManager(), options, new RoundReferenceHolder());

    if (!root.getName().toLowerCase().equals("format"))
      System.out.println(
          "Expecting root element to be format. Got " + root.getName() + " instead!");

    for (Element round : root.getChildren()) format.addRound(RoundParser.parse(format, round));

    return format;
  }
}
