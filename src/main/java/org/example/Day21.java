//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.*;
import org.example.utils.Point;
import org.example.utils.Vector;

import java.util.*;
import java.util.List;

public class Day21
{
  public static long task2(StringInput input, int iterations)
  {
    long total = 0L;

    for (String line : input.asLines())
    {
      long cost = calculateCost(line, iterations);
      System.out.println(line + ": " + cost);
      total += cost;
    }

    return total;
  }

  //Calculate the cost of a given input with a given number of intermediate robots
  //(only counting robots on directional pads)
  public static long calculateCost(String numericKeypadInput, int numRobots)
  {
    NumericKeypad numericKeypad = new NumericKeypad();
    DirectionalKeypad directionalKeypad = new DirectionalKeypad();

    SegmentedPath path = new SegmentedPath(numericKeypadInput);
    List<SegmentedPath> directionalKeypadPaths = path.expand(numericKeypad);

    var lengthCache = Day21.mapPathsToExpandedPathLength(directionalKeypad, numRobots);

    Long bestCost = null;
    for (SegmentedPath directionalKeypadPath : directionalKeypadPaths)
    {
      long pathCost = 0L;
      for (PathSegment segment : directionalKeypadPath.getSegments())
      {
        pathCost += lengthCache.get(new Pair<>(segment, numRobots));
      }

      if(bestCost == null || pathCost < bestCost)
      {
        bestCost = pathCost;
      }
    }

    long keypadCost = Long.parseLong(numericKeypadInput.substring(0, numericKeypadInput.length() - 1));
    long inputCost = keypadCost * bestCost;
    return inputCost;
  }

  //Map smallest path sequences (i.e. a n-horizontal & m-vertical movement followed by an A) to its
  //length given the amount of times it is expanded (i.e. how many robots have to contribute to its input
  //in a chain up to a given depth). This cache will allow us to get the length of a path segment after
  //it has been expanded up to depth times.
  public static Map<Pair<PathSegment, Integer>, Long>
    mapPathsToExpandedPathLength(Keypad keypad, int depth)
  {
    Map<Pair<PathSegment, Integer>, Long> results = new HashMap<>();
    List<SegmentedPath> startingPaths = Keypad.getAllStartingPaths().stream()
      .map(SegmentedPath::new)
      .toList();

    for(int i = 0; i < depth + 1; i++)
    {
      for (SegmentedPath startingPath : startingPaths)
      {
        PathSegment segment = startingPath.getSegments().getFirst();
        var key = new Pair<>(segment, i);
        assert startingPath.segments.size() == 1;

        //Zero is reserved for no expansion
        if(i == 0)
        {
          results.put(key, (long) segment.buttonPresses.size());
        }
        else
        {
          int finalI = i;
          //Expand each possible path, using the cache of the previous expansion to
          //quickly determine a sum that finds the full path length without needing
          //to actually expand the path
          long expandedPathLen = startingPath.expand(keypad)
            .stream()
            .mapToLong(expandedPath -> {
              long pathLen = 0l;
              for (PathSegment expandedSegment : expandedPath.getSegments())
              {
                var segmentKey = new Pair<>(expandedSegment, finalI - 1);
                pathLen += results.get(segmentKey);
              }
              return pathLen;
            })
            .min()
            .orElse(Long.MIN_VALUE);

          results.put(key, expandedPathLen);
        }
      }
    }

    return results;
  }

  //Convert list of characters to string
  private static String charListToString(List<Character> charList)
  {
    StringBuilder builder = new StringBuilder();

    for (Character c : charList)
    {
      builder.append(c);
    }

    return builder.toString();
  }

  //Convert string to list of characters
  private static List<Character> stringToCharList(String input)
  {
    List<Character> results = new ArrayList<>();
    for (char c : input.toCharArray())
    {
      results.add(c);
    }
    return results;
  }

  //Represents a full path made up of path segments
  public static class SegmentedPath
  {
    public SegmentedPath(PathSegment segment)
    {
      this.segments = List.of(segment);
    }

    public SegmentedPath(List<PathSegment> segments)
    {
      this.segments = segments;
    }

    public SegmentedPath(String path)
    {
      String[] tokens = path.split("A");
      this.segments = Arrays.stream(tokens)
        .map(str -> stringToCharList(str + "A"))
        .map(PathSegment::new)
        .toList();
    }

    //Expand a segmented path, this takes our current path & generates all the possible paths
    //another robot controlling this robot could potentionally take to generate our output
    public List<SegmentedPath> expand(Keypad keypad)
    {
      List<SegmentedPath> results = new ArrayList<>();
      expandRecurs(keypad, getAllCharacters(), 0, List.of(), results);
      return results;
    }

    //Recursive function to generate expansions
    private void expandRecurs(
      final Keypad keypad,
      final List<Character> characters,
      final int charIdx,
      List<PathSegment> curPath,
      List<SegmentedPath> results
    )
    {
      if(charIdx == characters.size())
      {
        SegmentedPath path = new SegmentedPath(curPath);
        results.add(path);
        return;
      }

      var expandedPaths = PathSegment.generate(keypad,
        charIdx >= 1 ? characters.get(charIdx - 1) : 'A',
        characters.get(charIdx)
      );

      for (PathSegment expandedPath : expandedPaths)
      {
        List<PathSegment> copy = new ArrayList<>(curPath);
        copy.add(expandedPath);
        expandRecurs(keypad, characters, charIdx + 1, copy, results);
      }
    }

    public List<Character> getAllCharacters()
    {
      return segments.stream().flatMap(path -> path.buttonPresses.stream()).toList();
    }

    public List<PathSegment> getSegments()
    {
      return segments;
    }

    @Override
    public boolean equals(Object o)
    {
      if (!(o instanceof SegmentedPath that))
      {
        return false;
      }
      return Objects.equals(segments, that.segments);
    }

    @Override
    public int hashCode()
    {
      return Objects.hashCode(segments);
    }

    @Override
    public String toString()
    {
      return charListToString(getAllCharacters());
    }

    private List<PathSegment> segments;
  }

  //Represent a section of the path that contains an optional horizontal movement in one direction, an
  //optional vertical movement in one direction followed by an A press. This is the pattern used for building
  //full paths that are joined to create the full segmented path.
  public static class PathSegment
  {
    public PathSegment()
    {
      this.buttonPresses = new ArrayList<>();
    }

    public PathSegment(List<Character> buttonPresses)
    {
      this.buttonPresses = buttonPresses;
    }

    //Generate a path segment by moving from a character on the keypad to another character on the keypad
    public static List<PathSegment> generate(Keypad keypad, char from, char to)
    {
      Point startPoint = keypad.getButtonPosition(from);
      Point endPoint = keypad.getButtonPosition(to);

      Vector vecTo = Vector.subtract(endPoint, startPoint);
      List<PathSegment> results = new ArrayList<>();

      int numHorizontal = (int)Math.abs(vecTo.getX());
      int numVertical = (int)Math.abs(vecTo.getY());

      if(numHorizontal == 0 && numVertical == 0)
      {
        PathSegment noMovePath = new PathSegment();
        noMovePath.buttonPresses.add('A');
        results.add(noMovePath);
        return results;
      }

      char horizontalButton = vecTo.getX() > 0 ? '>' : '<';
      char verticalButton = vecTo.getY() > 0 ? 'v' : '^';

      Point horizontalPoint = startPoint.add(Vector.makeXYvector(vecTo.getX(), 0));
      Point verticalPoint = startPoint.add(Vector.makeXYvector(0, vecTo.getY()));

      if(keypad.isInKeypad(horizontalPoint) && numHorizontal > 0)
      {
        PathSegment horizontalFirstPath = new PathSegment();
        horizontalFirstPath.addButtons(horizontalButton, numHorizontal);
        horizontalFirstPath.addButtons(verticalButton, numVertical);
        horizontalFirstPath.buttonPresses.add('A');
        results.add(horizontalFirstPath);
      }

      if(keypad.isInKeypad(verticalPoint) && numVertical > 0)
      {
        PathSegment verticalFirstPath = new PathSegment();
        verticalFirstPath.addButtons(verticalButton, numVertical);
        verticalFirstPath.addButtons(horizontalButton, numHorizontal);
        verticalFirstPath.buttonPresses.add('A');
        results.add(verticalFirstPath);
      }

      return results;
    }

    //Utility to add a given amount of button presses on the path segment
    private void addButtons(char button, int numTimes)
    {
      for(int i = 0; i < numTimes; i++)
      {
        buttonPresses.add(button);
      }
    }

    @Override
    public boolean equals(Object o)
    {
      if (!(o instanceof PathSegment path))
      {
        return false;
      }
      return Objects.equals(buttonPresses, path.buttonPresses);
    }

    @Override
    public int hashCode()
    {
      return Objects.hashCode(buttonPresses);
    }

    @Override
    public String toString()
    {
      return charListToString(buttonPresses);
    }

    public List<Character> buttonPresses;
  }

  //Data representation of a keypad
  public static abstract class Keypad
  {
    //Get the position of a button with a given character
    public abstract Point getButtonPosition(char button);

    //Get the possible positions of any buttons on the keypad
    public abstract Set<Point> getButtonPositions();

    //Get a list of characters that represent the buttons on the keypad
    public abstract List<Character> getAllButtons();

    //Check if point is a button on the keypad
    public boolean isInKeypad(Point point)
    {
      return getButtonPositions().contains(point);
    }

    //Utility to get the starting paths of all keypad types, mostly used to initialize caches
    public static List<PathSegment> getAllStartingPaths()
    {
      Set<PathSegment> paths = new HashSet<>();
      paths.addAll(new NumericKeypad().getStartingPaths());
      paths.addAll(new DirectionalKeypad().getStartingPaths());
      return paths.stream().toList();
    }

    //Gets all the starting path segments possible on this keypad
    // (i.e. a path from any keypad button to another other keypad button)
    public List<PathSegment> getStartingPaths()
    {
      List<PathSegment> results = new ArrayList<>();

      for (Character button1 : getAllButtons())
      {
        for (Character button2 : getAllButtons())
        {
          results.addAll(PathSegment.generate(this, button1, button2));
        }
      }

      return results;
    }
  }

  public static class DirectionalKeypad
    extends Keypad
  {
    @Override
    public Point getButtonPosition(char button)
    {
      return buttonPositionsMap.get(button);
    }

    @Override
    public Set<Point> getButtonPositions()
    {
      return buttonPositions;
    }

    @Override
    public List<Character> getAllButtons()
    {
      return buttons;
    }

    private static final List<Character> buttons = List.of('<', '>', '^', 'v', 'A');
    private static final Map<Character, Point> buttonPositionsMap;
    private static final Set<Point> buttonPositions;

    static {
      buttonPositionsMap = new HashMap<>();
      buttonPositionsMap.put('^', Point.makeXYPoint(1, 0));
      buttonPositionsMap.put('A', Point.makeXYPoint(2, 0));
      buttonPositionsMap.put('<', Point.makeXYPoint(0, 1));
      buttonPositionsMap.put('v', Point.makeXYPoint(1, 1));
      buttonPositionsMap.put('>', Point.makeXYPoint(2, 1));

      buttonPositions = new HashSet<>(buttonPositionsMap.values());
    }
  }

  public static class NumericKeypad
    extends Keypad
  {
    @Override
    public Point getButtonPosition(char button)
    {
      return buttonPositionsMap.get(button);
    }

    @Override
    public Set<Point> getButtonPositions()
    {
      return buttonPositions;
    }

    @Override
    public List<Character> getAllButtons()
    {
      return buttons;
    }

    private static final List<Character> buttons = List.of(
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A'
    );
    private static final Map<Character, Point> buttonPositionsMap;
    private static final Set<Point> buttonPositions;

    static {
      buttonPositionsMap = new HashMap<>();
      buttonPositionsMap.put('1', Point.makeXYPoint(0, 2));
      buttonPositionsMap.put('2', Point.makeXYPoint(1, 2));
      buttonPositionsMap.put('3', Point.makeXYPoint(2, 2));
      buttonPositionsMap.put('4', Point.makeXYPoint(0, 1));
      buttonPositionsMap.put('5', Point.makeXYPoint(1, 1));
      buttonPositionsMap.put('6', Point.makeXYPoint(2, 1));
      buttonPositionsMap.put('7', Point.makeXYPoint(0, 0));
      buttonPositionsMap.put('8', Point.makeXYPoint(1, 0));
      buttonPositionsMap.put('9', Point.makeXYPoint(2, 0));
      buttonPositionsMap.put('0', Point.makeXYPoint(1, 3));
      buttonPositionsMap.put('A', Point.makeXYPoint(2, 3));

      buttonPositions = new HashSet<>(buttonPositionsMap.values());
    }
  }
}
