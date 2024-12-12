//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.*;
import org.example.utils.Vector;

import java.util.*;

public class Day12
{
  private static boolean debug = false;

  public static long task1(StringInput input, boolean debug)
  {
    Day12.debug = debug;
    FlowerMap map = parseMap(input);
    return map.getFenceCost();
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day12.debug = debug;
    FlowerMap map = parseMap(input);
    return map.getDiscountedFenceCost();
  }

  //Parse map
  public static FlowerMap parseMap(StringInput input)
  {
    List<String> lines = input.asLines();
    FlowerMap map = new FlowerMap();
    map.width = lines.getFirst().length();
    map.height = lines.size();
    map.plants = new char[map.height][map.width];

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
        map.plants[i][j] = c;
      }
    }

    map.regions = createRegions(map);

    return map;
  }

  //Construct regions by finding connected plants
  public static List<Region> createRegions(FlowerMap map)
  {
    List<Region> regions = new ArrayList<>();
    Set<Point> unvisited = new HashSet<>();

    //Initialize unvisited points
    //Iterate rows
    for(int i = 0; i < map.height; i++)
    {
      //Iterate columns
      for(int j = 0 ; j < map.width; j++)
      {
        unvisited.add(new Point(i, j));
      }
    }


    while(!unvisited.isEmpty())
    {
      Point unvisitedPoint = unvisited.stream().findFirst().get();
      Region region = new Region();
      char flowerType = map.getFlowerType(unvisitedPoint);

      List<Point> regionPoints = getAllRegionPoints(map, flowerType, unvisitedPoint);
      unvisited.removeAll(regionPoints);

      region.flowerType = flowerType;
      region.regionPoints = new HashSet<>(regionPoints);
      regions.add(region);
    }

    return regions;
  }

  //Given a point finds its surrounding points. If checkInMap will give only points in the map, otherwise it
  //will just give all possible points
  private static List<Point> getSurroundingPoints(FlowerMap map, Point startPoint, boolean checkInMap)
  {
    List<Point> results = new ArrayList<>();

    for (Vector possibleDirection : POSSIBLE_DIRECTIONS)
    {
      Point pt = startPoint.add(possibleDirection);
      if(!checkInMap || map.isInMap(pt))
      {
        results.add(pt);
      }
    }

    return results;
  }

  //Get all points in the same region using DFS to search the map
  public static List<Point> getAllRegionPoints(FlowerMap map, char flowerType, Point startPoint)
  {
    List<Point> results = new ArrayList<>();
    depthFirstSearch(map, flowerType, startPoint, new HashSet<>(List.of(startPoint)), results);
    return results;
  }

  //Find all points in a region using DFS
  private static void depthFirstSearch(FlowerMap map, char flowerType, Point curPoint, Set<Point> visited, List<Point> results)
  {
    results.add(curPoint);

    List<Point> nextPoints = getSurroundingPoints(map, curPoint, true);

    var iter = nextPoints.iterator();
    while(iter.hasNext())
    {
      Point pt = iter.next();
      if(visited.contains(pt))
      {
        iter.remove();
      }
      else
      {
        visited.add(pt);
      }
    }

    for (Point nextPoint : nextPoints)
    {
      if(map.getFlowerType(nextPoint) == flowerType)
      {
        depthFirstSearch(map, flowerType, nextPoint, visited, results);
      }
    }
  }

  public static class Region
  {
    //Get the area of a region
    public int getArea()
    {
      return regionPoints.size();
    }

    //Get the fence perimiter of a region
    public int getPerimeter()
    {
      int perimeter = 0;
      for (Point regionPoint : regionPoints)
      {
        //Every surrounding point that doesn't neighbour a region point will have a fence
        List<Point> surroundingPoints = getSurroundingPoints(null, regionPoint, false);
        int nFences = POSSIBLE_DIRECTIONS.size() - (int)surroundingPoints.stream()
          .filter(this::hasPoint)
          .count();

        perimeter += nFences;
      }

      return perimeter;
    }

    //Check if point is in this region
    public boolean hasPoint(Point point)
    {
      return regionPoints.contains(point);
    }

    //Get the bounding box of a region to make searching within that region easier
    public Bounds getBounds()
    {
      Bounds bounds = new Bounds();
      int smallestCol = Integer.MAX_VALUE;
      int highestCol = 0;
      int smallestRow = Integer.MAX_VALUE;
      int highestRow = 0;

      for (Point regionPoint : regionPoints)
      {
        smallestCol = Math.min(smallestCol, regionPoint.col);
        highestCol = Math.max(highestCol, regionPoint.col);
        smallestRow = Math.min(smallestRow, regionPoint.row);
        highestRow = Math.max(highestRow, regionPoint.row);
      }

      bounds.startRow = smallestRow;
      bounds.startCol = smallestCol;
      bounds.width = highestCol - smallestCol + 1;
      bounds.height = highestRow - smallestRow + 1;

      return bounds;
    }

    //Get the number of fence sides of the region
    public int getNumSides()
    {
      Bounds bounds = getBounds();

      //We use transitions rather than the position of the fence because
      //if points are diagnoal to eachother but in the same region they will
      //share a fence border (as drawn in a straight line) but it will count
      //as 2 sides for the purpose of this exercise
      Set<FenceTransition> prevSideFencePosition = new HashSet<>();
      Set<FenceTransition> currentSideFencePosition = new HashSet<>();

      int nSides = 0;

      //Iterate looking for edges on the vertical directions (i.e. between rows)
      //Iteration is done by rows first, followed by columns
      for(int i = 0; i < bounds.width; i++)
      {
        prevSideFencePosition = currentSideFencePosition;
        currentSideFencePosition = new HashSet<>();
        boolean prevPointExisted = false;

        //Go one extra to check for fence from region point to nothing space
        for(int j = 0 ; j < bounds.height + 1; j++)
        {
          Point curPoint = bounds.getRelativePoint(j, i);
          //If this is a point in the region
          if(hasPoint(curPoint))
          {
            //If there was no point previously, then there should be a fence here
            if(!prevPointExisted)
            {
              var transition = FenceTransition.make(j, prevPointExisted);
              currentSideFencePosition.add(transition);
              //If there was a fence here in the previous row, then there is no new side
              if(!prevSideFencePosition.contains(transition))
              {
                nSides++;
              }

              prevPointExisted = true;
            }
          }
          //There is no point here
          else
          {
            //If previous was a point previously, but none here. Should be a fence here
            if(prevPointExisted)
            {
              var transition = FenceTransition.make(j, prevPointExisted);
              currentSideFencePosition.add(transition);
              //If there was a fence here in the previous row, then there is no new side
              if(!prevSideFencePosition.contains(transition))
              {
                nSides++;
              }

              prevPointExisted = false;
            }
          }
        }
      }

      //Reset side fence positions from vertical traversal
      currentSideFencePosition = new HashSet<>();

      //Iterate horizontally from left to right
      for(int i = 0; i < bounds.height; i++)
      {
        prevSideFencePosition = currentSideFencePosition;
        currentSideFencePosition = new HashSet<>();
        boolean prevPointExisted = false;

        //Go one extra to check for fence from region point to nothing space
        for(int j = 0 ; j < bounds.width + 1; j++)
        {
          Point curPoint = bounds.getRelativePoint(i, j);
          //If this is a point in the region
          if(hasPoint(curPoint))
          {
            //If there was no point previously, then there should be a fence here
            if(!prevPointExisted)
            {
              var transition = FenceTransition.make(j, prevPointExisted);
              currentSideFencePosition.add(transition);
              //If there was a fence here in the previous row, then there is no new side
              if(!prevSideFencePosition.contains(transition))
              {
                nSides++;
              }

              prevPointExisted = true;
            }
          }
          //There is no point here
          else
          {
            //If previous was a point previously, but none here. Should be a fence here
            if(prevPointExisted)
            {
              var transition = FenceTransition.make(j, prevPointExisted);
              currentSideFencePosition.add(transition);
              //If there was a fence here in the previous row, then there is no new side
              if(!prevSideFencePosition.contains(transition))
              {
                nSides++;
              }

              prevPointExisted = false;
            }
          }
        }
      }

      return nSides;
    }

    private static class FenceTransition
    {
      public static FenceTransition make(int idx, boolean prevExisted)
      {
        FenceTransition transition = new FenceTransition();
        transition.idx = idx;
        transition.prevExisted = prevExisted;
        return transition;
      }

      @Override
      public boolean equals(Object o)
      {
        if (!(o instanceof FenceTransition that))
        {
          return false;
        }
        return idx == that.idx && prevExisted == that.prevExisted;
      }

      @Override
      public int hashCode()
      {
        return Objects.hash(idx, prevExisted);
      }

      private int idx = 0;
      private boolean prevExisted = false;
    }

    public long getCost()
    {
      return (long) getArea() * getPerimeter();
    }

    public long getDiscountedCost()
    {
      return (long) getArea() * getNumSides();
    }

    char flowerType;
    Set<Point> regionPoints = new HashSet<>();
  }

  public static class FlowerMap
  {
    //Check if position is in map
    public boolean isInMap(Point position)
    {
      return position.row >= 0 && position.col >= 0 &&
        position.row < height && position.col < width;
    }

    //Get the character used in this grid point
    public char getFlowerType(Point point)
    {
      return plants[point.row][point.col];
    }

    public long getFenceCost()
    {
      long total = 0;
      for (Region region : regions)
      {
        total += region.getCost();
      }
      return total;
    }

    public long getDiscountedFenceCost()
    {
      long total = 0;
      for (Region region : regions)
      {
        total += region.getDiscountedCost();
      }
      return total;
    }

    private int width;
    private int height;
    private char[][] plants;
    private List<Region> regions;
  }

  public static class Bounds
  {
    //Get point relative to this bounding box to make iterating through
    //all the points inside these bounds a lot easier
    public Point getRelativePoint(int row, int col)
    {
      return new Point(startRow + row, startCol + col);
    }

    private int startCol;
    private int startRow;
    private int width;
    private int height;
  }

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
