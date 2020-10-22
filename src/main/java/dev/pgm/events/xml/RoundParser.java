package dev.pgm.events.xml;

import dev.pgm.events.format.Tournament;
import dev.pgm.events.format.rounds.RoundSettings;
import dev.pgm.events.format.rounds.TournamentRound;
import dev.pgm.events.format.rounds.format.FormatRound;
import dev.pgm.events.format.rounds.format.FormatRoundSettings;
import dev.pgm.events.format.rounds.resultfrom.ResultFromRound;
import dev.pgm.events.format.rounds.resultfrom.ResultFromSettings;
import dev.pgm.events.format.rounds.single.SingleRound;
import dev.pgm.events.format.rounds.single.SingleRoundSettings;
import dev.pgm.events.format.rounds.veto.VetoRound;
import dev.pgm.events.format.rounds.veto.settings.VetoOption;
import dev.pgm.events.format.rounds.veto.settings.VetoSettings;
import dev.pgm.events.format.rounds.vetoselector.VetoSelectorRound;
import dev.pgm.events.format.rounds.vetoselector.VetoSelectorSettings;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom2.Element;

public class RoundParser {

  public static TournamentRound parse(Tournament format, Element round) {
    switch (round.getName().toLowerCase()) {
      case "match":
        return SingleParser.parse(format, round);
      case "veto":
        return VetoParser.parse(format, round);
      case "result-from":
        return ResultFromParser.parse(format, round);
      case "format":
        return FormatParser.parse(format, round);
      case "veto-selector":
        return new VetoSelectorRound(format, new VetoSelectorSettings());
    }

    throw new IllegalArgumentException("Round " + round.getName() + " is not supported!");
  }

  public static class SingleParser {

    public static SingleRound parse(Tournament format, Element element) {
      String id =
          element.getAttributeValue("id", element.getValue().toLowerCase().replace(" ", "_"));
      String map = element.getValue();

      Duration cycleCountdown = Duration.ofSeconds(20);
      Duration startCountdown = Duration.ofSeconds(300);

      SingleRoundSettings options =
          new SingleRoundSettings(id, cycleCountdown, startCountdown, map, 1, true, true);
      return new SingleRound(format, options);
    }
  }

  public static class VetoParser {

    public static VetoRound parse(Tournament format, Element element) {
      String id = element.getAttributeValue("id", "veto");
      Element order = element.getChild("order");
      if (order == null) throw new IllegalArgumentException("Order element is missing from veto!");
      Duration vetoTime = Duration.ofSeconds(30);
      try {
        vetoTime = Duration.ofSeconds(Long.parseLong(order.getAttributeValue("time", "30")));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "Invalid time! Times are provided in seconds with no units.");
      }

      TournamentRound decider =
          RoundParser.parse(format, element.getChild("decider").getChildren().get(0));

      List<VetoOption> options = constructVetoOptions(format, element.getChild("options"));
      List<VetoSettings.Veto> vetoList = constructVetoList(order, options.size());

      VetoSettings settings =
          new VetoSettings(id, vetoTime, decider.settings(), 0, false, true, vetoList, options);
      return new VetoRound(format, settings);
    }

    private static List<VetoSettings.Veto> constructVetoList(Element element, int numOptions) {
      ArrayList<VetoSettings.Veto> vetos = new ArrayList<VetoSettings.Veto>();
      if (element.getChildren().size() == 0) {
        int until = Integer.parseInt(element.getAttributeValue("ban-until", "1"));
        int startingTeam = Integer.parseInt(element.getAttributeValue("starting-team", "2"));
        int numTeams = Integer.parseInt(element.getAttributeValue("teams", "2"));

        int currentTeam = startingTeam;
        for (int i = numOptions; i > until; i--) {
          vetos.add(new VetoSettings.Veto(VetoSettings.VetoType.BAN, currentTeam, null, true));
          currentTeam = (currentTeam + 1) % numTeams;
          if (currentTeam == 0) currentTeam += numTeams;
        }

        for (int i = 1; i < until; i++) {
          vetos.add(
              new VetoSettings.Veto(VetoSettings.VetoType.CHOOSE_LAST, currentTeam, null, true));
          currentTeam = (currentTeam + 1) % numTeams;
          if (currentTeam == 0) currentTeam += numTeams;
        }

        vetos.add(new VetoSettings.Veto(VetoSettings.VetoType.CHOOSE_LAST, 0, null, false));
      } else {
        for (Element child : element.getChildren()) vetos.add(parseVeto(child));
      }

      return vetos;
    }

    private static VetoSettings.Veto parseVeto(Element element) {
      VetoSettings.VetoType type = null;
      if (element.getName().equalsIgnoreCase("ban")) {
        type = VetoSettings.VetoType.BAN;
      } else if (element.getName().equalsIgnoreCase("pick")) {
        String insert = element.getAttributeValue("insert", "back");
        if (insert.equalsIgnoreCase("back")) type = VetoSettings.VetoType.CHOOSE_LAST;
        else if (insert.equalsIgnoreCase("front")) type = VetoSettings.VetoType.CHOOSE_FIRST;
        else
          throw new IllegalArgumentException(
              "Invalid insert position "
                  + insert
                  + ". Valid positions are back (default) or front.");
      }

      if (type == null)
        throw new IllegalArgumentException("Invalid veto order " + element.getName());

      int team = Integer.parseInt(element.getAttributeValue("team", "0"));
      return new VetoSettings.Veto(type, team, null, true);
    }

    private static List<VetoOption> constructVetoOptions(Tournament format, Element element) {
      ArrayList<VetoOption> options = new ArrayList<VetoOption>();

      for (Element child : element.getChildren()) {
        RoundSettings round = RoundParser.parse(format, child).settings();
        String defaultName = "Name not defined in XML!";
        if (round instanceof SingleRoundSettings) defaultName = ((SingleRoundSettings) round).map();

        options.add(
            new VetoOption(Arrays.asList(round), child.getAttributeValue("name", defaultName)));
      }

      return options;
    }
  }

  public static class ResultFromParser {

    public static ResultFromRound parse(Tournament format, Element element) {
      String target = element.getAttributeValue("id");

      ResultFromSettings options = new ResultFromSettings(target);
      return new ResultFromRound(format, options);
    }
  }

  public static class FormatParser {

    public static FormatRound parse(Tournament format, Element element) {
      String name = element.getAttributeValue("name", "Format");
      String id = element.getAttributeValue("id", name.toLowerCase());

      String bestOfArgs = element.getAttributeValue("best-of");
      if (bestOfArgs == null) throw new IllegalArgumentException("No best-of specified on format!");
      int bestOf = Integer.parseInt(bestOfArgs);

      List<RoundSettings> rounds = new ArrayList<RoundSettings>();
      for (Element child : element.getChildren()) {
        TournamentRound round = RoundParser.parse(format, child);
        rounds.add(round.settings());
      }

      FormatRoundSettings settings = new FormatRoundSettings(id, name, rounds, bestOf);
      return new FormatRound(format, settings);
    }
  }
}
