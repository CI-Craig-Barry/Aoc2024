//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package org.example;

import org.example.utils.*;
import org.example.utils.Vector;

import java.util.*;

public class Day18
{
  public static boolean debug;

  public static long task1(StringInput input, int gridSize, int limit, boolean debug)
  {
    Day18.debug = debug;

    MemorySpace memory = parseMemorySpace(input, gridSize, limit);
    if(debug)
    {
      System.out.println("*** Starting Map ***");
      memory.draw(Set.of());
      System.out.println("*** End map ***");
    }

    var result = astar(memory);

    if(debug)
    {
      System.out.println("*** Final Map, cost: " + result.getValue() + " ***");
      memory.draw(new HashSet<>(result.getKey()));
      System.out.println("*** End map ***");
    }

    return result.getKey().size() - 1;
  }

  public static String task2(StringInput input, int gridSize, boolean debug)
  {
    Day18.debug = debug;

    int nBytes = input.asLines().size();

    for(int i = 1; i < nBytes; i++)
    {
      MemorySpace memory = parseMemorySpace(input, gridSize, i);
      var result = astar(memory);

      if(result.getKey().isEmpty())
      {
        String addedCoord = input.asLines().get(i-1);

        if(debug)
        {
          System.out.println("Found no solution adding " + addedCoord + " at n points: " + i);
          memory.draw(Set.of());
          System.out.println("*** End Map***");
          System.out.println();

          System.out.println("*** Previous Map***");
          MemorySpace prevMemory = parseMemorySpace(input, gridSize, i - 1);
          var path = astar(prevMemory);
          prevMemory.draw(Set.of());
          System.out.println("*** End Map ***");
          System.out.println();
        }

        return addedCoord;
      }
    }

    return null;
  }

  public static MemorySpace parseMemorySpace(StringInput input, int gridSize, int limit)
  {
    MemorySpace memorySpace = new MemorySpace();
    memorySpace.width = gridSize;
    memorySpace.height = gridSize;
    memorySpace.spaces = new HashMap<>();
    memorySpace.end = Point.makeXYPoint(gridSize - 1, gridSize - 1);

    int lines = 0;
    for (String line : input.asLines())
    {
      if(lines == limit)
      {
        break;
      }

      String[] tokens = line.split(",");
      int x = Integer.parseInt(tokens[0]);
      int y = Integer.parseInt(tokens[1]);
      Point pt = Point.makeXYPoint(x, y);

      memorySpace.spaces.put(pt, SpaceType.CORRUPTED);

      lines++;
    }

    return memorySpace;
  }

  public static Pair<List<Point>, Long> astar(MemorySpace memorySpace)
  {
    //Create map of all empty squares to astar nodes
    Map<Point, AStarNode> nodeMap = new HashMap<>();

    //Sort open list by f score
    PriorityQueue<AStarNode> openList = new PriorityQueue<>(
      Comparator.comparingLong(v -> v.f)
    );

    Long endCost = null;

    //Add starting point to the open list, we start facing east
    AStarNode startNode = AStarNode.makeNull(memorySpace, memorySpace.start);
    nodeMap.put(memorySpace.start, startNode);
    startNode.setG(null, 0);
    openList.add(startNode);

    //Iterate until open list is empty
    while (!openList.isEmpty())
    {
      AStarNode current = openList.poll();
      if (current.pos.equals(memorySpace.end))
      {
        endCost = current.f;
      }

      current.open = false;
      current.closed = true;

      List<AStarNode> neighbours = memorySpace.getSafeNeighbours(current.pos)
        .stream()
        .map(neighbourState -> {
          var result = nodeMap.get(neighbourState);
          if (result != null)
          {
            return result;
          }

          var astarNode = AStarNode.makeNull(memorySpace, neighbourState);
          nodeMap.put(neighbourState, astarNode);
          return astarNode;
        })
        .toList();

      for (AStarNode neighbour : neighbours)
      {
        //Point is no closed, no need to check
        //Usually in A* we would skip closed neighbours. But we are looking for
        //Paths that can also end up at the same state with the same cost to be
        //able to find all shortest paths
        if (neighbour.closed)
        {
          continue;
        }

        //Calculate cost for travelling from current to the given neighbour
        long g = AStarNode.calculateG(neighbour, current);

        //If this neighbour is on the open list, check if the current score traversing from
        //the current node rather than its previous parent is better
        if (neighbour.open)
        {
          //If our score is better, override the node
          if (g < neighbour.g)
          {
            neighbour.setG(current, g);

            //Delete & re-add to the openlist to facilitate sorting as its f score has changed
            if (g != neighbour.g)
            {
              openList.remove(neighbour);
              openList.add(neighbour);
            }
          }
        }
        //Node is not open, give it an initial score
        else
        {
          neighbour.setG(current, g);
          openList.add(neighbour);
        }
      }
    }

//    Set<Point> allPaths = new HashSet<>();
//    for (Day16.State endState : endStates)
//    {
//      unwindPathRecur(nodeMap.get(endState), allPaths);
//    }
//
//    if (debug)
//    {
//      System.out.println("*** Showing checked cells ***");
//      maze.drawMap(
//        nodeMap,
//        allPaths
//      );
//      System.out.println("*** End map ***");
//      System.out.print("Count: " + nodeMap.size());
//
//      int c = 0;
//      for(int x = 0; x < maze.width; x++)
//      {
//        for(int y = 0; y < maze.height; y++)
//        {
//          c += maze.getType(Point.makeXYPoint(x, y)) == Day16.SpaceType.EMPTY ? 4 : 0;
//        }
//      }
//
//      System.out.print("Max Count: " + c);
//    }
//
//    return new Pair<>(allPaths, nodeMap.get(endStates.stream().findAny().orElse(null)).g);
    List<Point> path = unwindPath(nodeMap.get(memorySpace.end));
    return new Pair<>(path, endCost);
  }

  public static List<Point> unwindPath(AStarNode end)
  {
    List<Point> points = new ArrayList<>();
    AStarNode iter = end;
    while(iter != null)
    {
      points.add(iter.pos);
      iter = iter.parent;
    }

    return points;
  }

  private static class MemorySpace
  {
    public boolean isInSpace(Point pt)
    {
      return pt.row >= 0 && pt.col >= 0 &&
        pt.row < height && pt.col < width;
    }

    public SpaceType getType(Point pt)
    {
      SpaceType type = spaces.get(pt);
      return type == null ? SpaceType.SAFE : type;
    }

    public List<Point> getSafeNeighbours(Point point)
    {
      List<Point> points = new ArrayList<>();

      for (Vector cardinalDirection : Vector.CARDINAL_DIRECTIONS)
      {
        Point neighbour = point.add(cardinalDirection);
        boolean safeNeighbour = isInSpace(neighbour) && getType(neighbour) == SpaceType.SAFE;
        if(safeNeighbour)
        {
          points.add(neighbour);
        }
      }

      return points;
    }

    public void draw(Set<Point> path)
    {
      StringBuilder builder = new StringBuilder();

      for(int y = 0; y < height; y++)
      {
        for(int x = 0; x < width; x++)
        {
          Point pt = Point.makeXYPoint(x, y);
          SpaceType type = getType(pt);
          char c = ',';

          if(type == SpaceType.CORRUPTED)
          {
            c = '#';
          }
          else if(path.contains(pt))
          {
            c = 'O';
          }

          builder.append(c);
        }

        builder.append("\n");
      }

      System.out.print(builder.toString());
    }


    private Map<Point, SpaceType> spaces;

    private Point start = Point.makeXYPoint(0, 0);
    private Point end;

    private int width;
    private int height;
  }

  public static class AStarNode
  {
    //Construct a node with no cost or F value
    public static AStarNode makeNull(MemorySpace memorySpace, Point point)
    {
      var node = new AStarNode();
      node.pos = point;
      node.open = false;
      node.closed = false;
      node.parent = null;
      node.h = calcHeuristic(memorySpace, point);

      return node;
    }

    //Calculate heuristic for this node
    public static int calcHeuristic(MemorySpace memorySpace, Point point)
    {
      return (int) Vector.manhattanDistance(point, memorySpace.end);
    }

    //Set the cost of this node
    public void setG(AStarNode parent, long g)
    {
      this.open = true;
      this.g = g;
      this.f = g + h;
      this.parent = parent;
    }

    //Calculate the code of traversing from the parent state to the current state
    public static long calculateG(AStarNode current, AStarNode parent)
    {
      return parent.g + 1;
    }

    private AStarNode parent;
    private Point pos;
    private boolean open;
    private boolean closed;
    private long f; // G + H
    private long g; // Cost to get to this node
    private long h; // Heuristic cost to get to the end
  }

  private enum SpaceType
  {
    SAFE,
    CORRUPTED
  }
}
