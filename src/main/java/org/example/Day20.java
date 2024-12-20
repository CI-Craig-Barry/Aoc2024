package org.example;

import org.example.utils.*;
import org.example.utils.Vector;
import org.w3c.dom.html.HTMLHeadElement;

import java.util.*;
import java.util.stream.Collectors;

public class Day20
{
  private static boolean debug;

  public static long task1(StringInput input, boolean debug)
  {
    Day20.debug = debug;

    RaceTrack raceTrack = parseRaceTrack(input);
    Map<Point, Integer> distanceCosts = getDistancesFromEnd(raceTrack);
    List<Cheat> cheats = getAllCheats(raceTrack, distanceCosts);

    if(debug)
    {
      cheats.sort(Comparator.comparingInt(v -> v.picoSaved));
      for (Cheat cheat : cheats)
      {
        System.out.println(cheat);
      }
    }

    int n100SecondCheats = 0;
    for (Cheat cheat : cheats)
    {
      n100SecondCheats += cheat.picoSaved >= 100 ? 1 : 0;
    }

    return n100SecondCheats;
  }

  public static long task2(StringInput input, int minCheatDistance, int cheatSize, boolean debug)
  {
    Day20.debug = debug;

    RaceTrack raceTrack = parseRaceTrack(input);
    Map<Point, Integer> pointToDistanceMap = getDistancesFromEnd(raceTrack);
    Map<Integer, Point> distanceToPointMap = getDistancesFromEndReversed(pointToDistanceMap);
    int maxCost = distanceToPointMap.keySet().stream()
      .max(Comparator.comparingInt(v -> v))
      .orElse(0);

    List<Cheat> cheats = new ArrayList<>();

    for(int x = 1; x < raceTrack.width - 1; x++)
    {
      for (int y = 1; y < raceTrack.height - 1; y++)
      {
        Point pt = Point.makeXYPoint(x, y);
        if (raceTrack.isEmpty(pt))
        {
          int distanceCost = pointToDistanceMap.get(pt);

          for(int cost = distanceCost + minCheatDistance; cost < maxCost + 1; cost++)
          {
            Point startPoint = distanceToPointMap.get(cost);
            long distanceFromPoints = Vector.manhattanDistance(pt, startPoint);
            int costSaved = cost - distanceCost - (int)distanceFromPoints;

            //If distance is within 20 points, this is a real cheat
            if(distanceFromPoints <= cheatSize && costSaved >= minCheatDistance)
            {
              cheats.add(new Cheat(
                costSaved,
                startPoint, null, pt
              ));
            }
          }
        }
      }
    }

    if(debug)
    {
      cheats.sort(Comparator.comparingInt(v -> v.picoSaved));

      Map<Integer, Integer> savedSecondsCount = new HashMap<>();
      for (Cheat cheat : cheats)
      {
        int count = savedSecondsCount.getOrDefault(cheat.picoSaved, 0);
        savedSecondsCount.put(cheat.picoSaved, count + 1);
      }

      List<String> entries = savedSecondsCount.entrySet()
        .stream()
        .sorted(Comparator.comparingInt(Map.Entry::getKey))
        .map(v -> v.getKey() + ": " + v.getValue())
        .toList();

      entries.forEach(System.out::println);
    }

    return cheats.size();
  }

  public static List<Cheat> getAllCheats(RaceTrack raceTrack, Map<Point, Integer> distanceFromEnd)
  {
    List<Cheat> cheats = new ArrayList<>();

    for(int x = 1; x < raceTrack.width - 1; x++)
    {
      for(int y = 1; y < raceTrack.height - 1; y++)
      {
        Point pt = Point.makeXYPoint(x, y);
        if(raceTrack.isWall(pt))
        {
          cheats.addAll(getCheatsFromWall(raceTrack, distanceFromEnd, pt));
        }
      }
    }

    return cheats;
  }

  private static List<Cheat> getCheatsFromWall(RaceTrack raceTrack, Map<Point, Integer> distanceFromEnd, Point wallPoint)
  {
    List<Cheat> cheats = new ArrayList<>();

    Cheat horizontalCheat = findDirectionalCheat(raceTrack, distanceFromEnd, wallPoint, Vector.EAST);
    Cheat verticalCheat = findDirectionalCheat(raceTrack, distanceFromEnd, wallPoint, Vector.SOUTH);

    if(horizontalCheat != null)
    {
      cheats.add(horizontalCheat);
    }

    if(verticalCheat != null)
    {
      cheats.add(verticalCheat);
    }

    return cheats;
  }

  private static Cheat findDirectionalCheat(RaceTrack raceTrack, Map<Point, Integer> distanceFromEnd, Point wallPoint, Vector direction)
  {
    Vector oppositeDirection = direction.reverse();
    Point pt1 = wallPoint.add(direction);
    Point pt2 = wallPoint.add(oppositeDirection);

    //If both points are empty, then there is a cheat here
    if(raceTrack.isEmpty(pt1) && raceTrack.isEmpty(pt2))
    {
      int cost1 = distanceFromEnd.get(pt1);
      int cost2 = distanceFromEnd.get(pt2);

      //Ensure costs to be cost1 is < cost2
      if(cost2 > cost1)
      {
        //Swap points
        Point tmpPt = pt2;
        pt2 = pt1;
        pt1 = tmpPt;
      }

      int savedDiff = Math.abs(cost2 - cost1);
      //Got to account for the movement of actually performing the cheat
      savedDiff -= 2;

      Cheat cheat = new Cheat(
        savedDiff,
        pt2,
        wallPoint,
        pt1
      );

      return cheat;
    }

    return null;
  }

  private static RaceTrack parseRaceTrack(StringInput input)
  {
    RaceTrack map = new RaceTrack();
    List<String> lines = input.asLines();
    map.width = lines.getFirst().length();
    map.height = lines.size();
    map.spaces = new HashMap<>();

    for(int i = 0; i < lines.size(); i++)
    {
      String line = lines.get(i).trim();
      char[] chars = line.toCharArray();
      assert chars.length == map.width;

      for(int j = 0 ; j < chars.length; j++)
      {
        char c = chars[j];

        if(c == '#')
        {
          Point pt = Point.makeXYPoint(j, i);
          map.spaces.put(pt, SpaceType.WALL);
        }
        else if(c == 'S')
        {
          Point pt = Point.makeXYPoint(j, i);
          map.start = pt;
        }
        else if(c == 'E')
        {
          Point pt = Point.makeXYPoint(j, i);
          map.end = pt;
        }
      }
    }

    return map;
  }

  //Get the score of each position on the racetrack as how far it is from the end
  private static Map<Point, Integer> getDistancesFromEnd(RaceTrack raceTrack)
  {
    Map<Point, Integer> distances = new HashMap<>();
    Queue<Point> pointQueue = new LinkedList<>();
    pointQueue.add(raceTrack.end);
    int cost = 0;
    distances.put(raceTrack.end, cost);

    //BFS Assigning distance from the finish to each empty space
    while(!pointQueue.isEmpty())
    {
      Point cur = pointQueue.poll();
      cost++;

      for (Vector cardinalDirection : Vector.CARDINAL_DIRECTIONS)
      {
        Point next = cur.add(cardinalDirection);

        if(!raceTrack.isWall(next) && !distances.containsKey(next))
        {
          distances.put(next, cost);
          pointQueue.add(next);
        }
      }
    }

    getDistanceFromEndRecur(raceTrack, 0, raceTrack.end, distances);
    return distances;
  }

  private static Map<Integer, Point> getDistancesFromEndReversed(Map<Point, Integer> pointToDistanceMap)
  {
    Map<Integer, Point> reversedMap = new HashMap<>();

    for (Map.Entry<Point, Integer> entry : pointToDistanceMap.entrySet())
    {
      reversedMap.put(entry.getValue(), entry.getKey());
    }

    return reversedMap;
  }

  //Recursively get positions
  private static void getDistanceFromEndRecur(RaceTrack raceTrack, int cost, Point position, Map<Point, Integer> results)
  {
    List<Point> candidates = Vector.CARDINAL_DIRECTIONS
      .stream()
      .map(position::add)
      .collect(Collectors.toCollection(ArrayList::new));

    var iter = candidates.iterator();
    while(iter.hasNext())
    {
      var candidate = iter.next();
      if(!results.containsKey(candidate) && !raceTrack.isWall(candidate))
      {
        results.put(candidate, cost + 1);
      }
      else
      {
        iter.remove();
      }
    }

    for (Point candidate : candidates)
    {
      getDistanceFromEndRecur(raceTrack, cost + 1, candidate, results);
    }
  }

  public static class Cheat
  {
    public Cheat(int picoSaved, Point prevPos, Point startPos, Point endPos)
    {
      this.picoSaved = picoSaved;
      this.prevPos = prevPos;
      this.startPos = startPos;
      this.endPos = endPos;
    }

    @Override
    public String toString()
    {
      return "Cheat{" +
        "picoSaved=" + picoSaved +
        ", prevPos=" + prevPos +
        ", startPos=" + startPos +
        ", endPos=" + endPos +
        '}';
    }

    private int picoSaved;
    private Point prevPos;
    private Point startPos; //Position of the wall
    private Point endPos; //Position at the end
  }

  public static class RaceTrack
  {
    //Check if position is in map
    public boolean isInMap(Point position)
    {
      return position.row >= 0 && position.col >= 0 &&
        position.row < height && position.col < width;
    }

    //Return true if coordinates are a wall (either placed or static)
    public boolean isWall(Point point)
    {
      return getType(point) == SpaceType.WALL;
    }

    public boolean isEmpty(Point pt)
    {
      return isInMap(pt) && (getType(pt) == SpaceType.EMPTY);
    }

    //Get type of point
    public SpaceType getType(Point point)
    {
      var type = spaces.get(point);
      return type == null ? SpaceType.EMPTY : type;
    }


    private int width;
    private int height;
    private Point start;
    private Point end;
    private Map<Point, SpaceType> spaces;
  }

  public enum SpaceType
  {
    EMPTY,
    WALL
  }
}
