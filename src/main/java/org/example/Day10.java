package org.example;

import org.example.utils.*;
import org.example.utils.Vector;

import java.util.*;

public class Day10
{
  private static boolean debug = false;

  public static long task1(StringInput input, boolean debug)
  {
    Day10.debug = debug;

    TopographicMap map = makeTopographicMap(input);
    int score = cullDuplicateTrails(map.findAllTrails()).size();
    return score;
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day10.debug = debug;

    TopographicMap map = makeTopographicMap(input);
    int rating = map.findAllTrails().size();
    return rating;
  }

  //Parse the topographic map
  public static TopographicMap makeTopographicMap(StringInput input)
  {
    TopographicMap result = new TopographicMap();
    List<String> lines = input.asLines();

    result.width = lines.getFirst().length();
    result.height = lines.size();
    result.map = new int[result.height][result.width];
    result.trailHeads = new ArrayList<>();

    for (int row = 0; row < result.height; row++)
    {
      for(int col = 0; col < result.width; col++)
      {
        char c = lines.get(row).charAt(col);
        int height = Integer.parseInt(String.valueOf(c));

        //If this is a trailhead, mark it
        if(height == TRAIL_START_HEIGHT)
        {
          Point trailHead = new Point(row, col);
          result.trailHeads.add(trailHead);
        }

        result.map[row][col] = height;
      }
    }

    return result;
  }

  //Cull trails which have the same start/end position
  public static List<Trail> cullDuplicateTrails(List<Trail> trails)
  {
    Map<String, Trail> uniqueTrails = new HashMap<>();

    for (Trail trail : trails)
    {
      uniqueTrails.put(trail.getStartEndIdentifier(), trail);
    }

    return new ArrayList<>(uniqueTrails.values());
  }

  private static class TopographicMap
  {
    //Check if position is in map
    public boolean isInMap(Point position)
    {
      return position.row >= 0 && position.col >= 0 &&
        position.row < height && position.col < width;
    }

    //Get height of position
    private int getHeight(Point position)
    {
      return map[(int)position.row][(int)position.col];
    }

    //Find all possible moves from a given position that are traversible
    //And go in an incline (i.e. their height is one greater than our current position)
    public List<Point> findPossibleInclineMoves(Point position)
    {
      List<Point> results = new ArrayList<>();

      int currentHeight = getHeight(position);

      POSSIBLE_DIRECTIONS.forEach(direction -> {
        Point newPoint = position.add(direction);

        if(isInMap(newPoint))
        {
          int newHeight = getHeight(newPoint);

          if(newHeight == currentHeight + 1)
          {
            results.add(newPoint);
          }
        }
      });

      return results;
    }

    //Find all trails
    public List<Trail> findAllTrails()
    {
      List<Trail> trails = new ArrayList<>();

      for (Point trailHead : trailHeads)
      {
        depthFirstSearch(Collections.singletonList(trailHead), trails);
      }

      //Draw trails for fun
      if (debug)
      {
        for (Trail trail : trails)
        {
          trail.drawTrail(this);
        }
      }

      return trails;
    }

    //Find all trails using DFS
    private void depthFirstSearch(List<Point> path, List<Trail> results)
    {
      Point curPoint = path.getLast();
      int curHeight = getHeight(curPoint);

      if(curHeight == TRAIL_END_HEIGHT)
      {
        var trail = new Trail();
        trail.path = new ArrayList<>(path);
        results.add(trail);
        return;
      }

      List<Point> nextPoints = findPossibleInclineMoves(curPoint);

      for (Point nextPoint : nextPoints)
      {
        List<Point> nextPath = new ArrayList<>(path);
        nextPath.add(nextPoint);
        depthFirstSearch(nextPath, results);
      }
    }

    private List<Point> trailHeads;

    private int width;
    private int height;
    private int[][] map;
  }

  private static class Trail
  {
    //Draw map for fun
    public void drawTrail(TopographicMap map)
    {
      StringBuilder builder = new StringBuilder();

      for(int row = 0; row < map.height; row++)
      {
        for(int col = 0; col < map.width; col++)
        {
          Point curPoint = new Point(row, col);
          int index = path.indexOf(curPoint);

          if(index != -1)
          {
            builder.append(index);
          }
          else
          {
            builder.append(".");
          }
        }
        builder.append('\n');
      }

      System.out.println("===START TRAIL===");
      System.out.println(builder.toString());
      System.out.println("===END TRAIL===");
    }

    //Get an identifier that uniquely marks a trail based on its start & end position.
    //If a trail has the same start & end as another (even if their paths are different).
    //They will have the same startEndIdentifier
    public String getStartEndIdentifier()
    {
      Point startPoint = path.getFirst();
      Point endPoint = path.getLast();

      return startPoint.toString() + endPoint.toString();
    }

    private List<Point> path;
  }

  private static final int TRAIL_START_HEIGHT = 0;
  private static final int TRAIL_END_HEIGHT = 9;


  private static final List<Vector> POSSIBLE_DIRECTIONS;

  //Calculate all traversible directions for later use
  static
  {
    POSSIBLE_DIRECTIONS = new ArrayList<>();
    POSSIBLE_DIRECTIONS.add(new Vector(1, 0));  //RIGHT
    POSSIBLE_DIRECTIONS.add(new Vector(0, 1));  //DOWN
    POSSIBLE_DIRECTIONS.add(new Vector(-1, 0)); //LEFT
    POSSIBLE_DIRECTIONS.add(new Vector(0, -1)); //UP
  }
}
