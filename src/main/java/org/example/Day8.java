package org.example;

import org.example.utils.*;
import org.example.utils.Vector;

import java.util.*;

public class Day8
{
  public static long task1(List<String> inputs)
  {
    Day8Map map = makeMap(inputs, false);
    map.drawMap();
    return map.antinodes.size();
  }

  public static long task2(List<String> inputs)
  {
    Day8Map map = makeMap(inputs, true);
    map.drawMap();
    return map.antinodes.size();
  }

  //Construct map from string input
  private static Day8Map makeMap(List<String> lines, boolean allAntinodes)
  {
    Day8Map map = new Day8Map();
    map.width = lines.getFirst().length();
    map.height = lines.size();
    map.attenas = new HashMap<>();

    //Iterate rows
    for(int i = 0; i < lines.size(); i++)
    {
      String line = lines.get(i).trim();
      char[] chars = line.toCharArray();

      assert chars.length == map.width;

      //Iterate columns
      for(int j = 0 ; j < chars.length; j++)
      {
        char c = chars[j];
        //If this is an antenna, map it to the antenna character
        if(Day8Map.isAntennaChar(c))
        {
          Point pt = new Point(i, j);
          map.attenas.put(pt, c);
        }
      }
    }

    //Calcualte the antinodes
    map.calculateAntinodes(allAntinodes);

    return map;
  }

  private static class Day8Map
  {
    public Day8Map()
    {

    }

    //Checks if a coordinate is within the bounds of the map
    public boolean isWithinBounds(Point point)
    {
      return point.row >= 0 && point.row < height &&
        point.col >= 0 && point.col < width;
    }

    //Check if the given character is an antenna
    public static boolean isAntennaChar(char c)
    {
      if(c == '.')
      {
        return false;
      }

      return Character.isDigit(c) || Character.isAlphabetic(c);
    }

    //Draw map for fun
    public void drawMap()
    {
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < height; i++)
      {
        for(int j = 0 ; j < width; j++)
        {
          char c = '.';
          Point point = new Point(i, j);
          Character attenaChar = attenas.get(point);

          if(attenaChar != null)
          {
            c = attenaChar;
          }
          else if(antinodes.contains(point))
          {
            c = '#';
          }

          builder.append(c);
        }
        builder.append('\n');
      }

      System.out.println("====START MAP===");
      System.out.println(builder.toString());
      System.out.println("====END   MAP===\n");
    }

    //Calculate antinodes
    public void calculateAntinodes(boolean allAntinodes)
    {
      this.antinodes = new HashSet<>();

      for (var entry : getSameFrequencyAntennas().entrySet())
      {
        List<Point> sameFrequencyAntennas = entry.getValue();
        List<Pair<Point, Point>> allAntennaPairs = Pair.makeAllPairs(sameFrequencyAntennas);

        for (Pair<Point, Point> antennaPair : allAntennaPairs)
        {
          Point pt1 = antennaPair.getFirst();
          Point pt2 = antennaPair.getSecond();
          Vector pointDiff = Vector.subtract(pt2, pt1);

          //Add the points as antinodes if we are calculating all antinodes
          //as they will always be antinodes
          if(allAntinodes)
          {
            this.antinodes.add(pt1);
            this.antinodes.add(pt2);
          }

          //Find all antinodes moving away from point 2
          Point antiNodeDirection1 = pt2;
          do
          {
            antiNodeDirection1 = antiNodeDirection1.add(pointDiff);
            if(isWithinBounds(antiNodeDirection1))
            {
              this.antinodes.add(antiNodeDirection1);
            }
            else
            {
              break;
            }
          }
          while(allAntinodes);

          //Find all antinodes moving away from point 1
          Point antiNodeDirection2 = pt1;
          do
          {
            antiNodeDirection2 = antiNodeDirection2.subtract(pointDiff);
            if(isWithinBounds(antiNodeDirection2))
            {
              this.antinodes.add(antiNodeDirection2);
            }
            else
            {
              break;
            }
          }
          while(allAntinodes);
        }
      }
    }

    //Map all attennas with the same frequency in a map of character to attenas
    private Map<Character, List<Point>> getSameFrequencyAntennas()
    {
      Map<Character, List<Point>> results = new HashMap<>();

      for (var entry : attenas.entrySet())
      {
        List<Point> ptList = results.get(entry.getValue());
        if(ptList == null)
        {
          ptList = new ArrayList<>();
        }
        ptList.add(entry.getKey());

        results.put(entry.getValue(), ptList);
      }

      return results;
    }

    private int width;
    private int height;
    private Map<Point, Character> attenas;
    private Set<Point> antinodes;
  }
}
