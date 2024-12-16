package org.example;//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

import org.example.utils.*;
import org.example.utils.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class Day16
{
  private static boolean debug;

  public static long task1(StringInput input, boolean debug)
  {
    Day16.debug = debug;

    Maze maze = parseMaze(input);
    if (debug)
    {
      System.out.println("*** Start Map ***");
      maze.drawMap(maze.start, Set.of());
      System.out.println("*** End Map ***");
    }

    var results = astar(maze);
    var path = results.getKey();
    var cost = results.getSecond();

    if (debug)
    {
      System.out.println("*** Final Map ***");
      maze.drawMap(maze.start, new HashSet<>(path));
      System.out.println("*** End Map ***");
    }

    return cost;
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day16.debug = debug;

    Maze maze = parseMaze(input);
    if (debug)
    {
      System.out.println("*** Start Map ***");
      maze.drawMap(maze.start, Set.of());
      System.out.println("*** End Map ***");
    }

    var results = astar(maze);
    var path = results.getKey();
    var cost = results.getSecond();

    if (debug)
    {
      System.out.println("*** Final Map ***");
      maze.drawMap(maze.start, new HashSet<>(path));
      System.out.println("*** End Map ***");
    }

    return path.size();
  }

  //Use A* search to find all paths from the starting position to the ending position. Return value is
  //a set of all points in each return both & the cost of taking one of those optimal paths
  public static Pair<Set<Point>, Long> astar(Maze maze)
  {
    //Create map of all empty squares to astar nodes
    Map<State, AStarNode> nodeMap = new HashMap<>();

    //Sort open list by f score
    PriorityQueue<AStarNode> openList = new PriorityQueue<>(
      Comparator.comparingLong(v -> v.f)
    );

    State startState = new State(maze.start, Vector.makeXYvector(1, 0));
    Long endCost = null;

    //Add starting point to the open list, we start facing east
    AStarNode startNode = AStarNode.makeNull(maze, startState);
    nodeMap.put(startState, startNode);
    startNode.setG(null, 0);
    openList.add(startNode);
    Set<State> endStates = new HashSet<>();

    //Iterate until open list is empty
    while (!openList.isEmpty())
    {
      AStarNode current = openList.poll();
      if(current.pos.equals(maze.end))
      {
        assert endCost == null || endCost == current.f;
        endCost = current.f;
        endStates.add(new State(current.pos, current.dir));
      }
      else if(endCost != null && current.f > endCost)
      {
        break;
      }

      current.open = false;
      current.closed = true;

      List<AStarNode> neighbours = getNextStates(maze, current)
        .stream()
        .map(neighbourState -> {
          var result = nodeMap.get(neighbourState);
          if(result != null)
          {
            return result;
          }

          var astarNode = AStarNode.makeNull(maze, neighbourState);
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
        if (neighbour.closed && endCost != null)
        {
          continue;
        }

        //Calculate cost for travelling from current to the given neighbour
        long g = AStarNode.calculateG(neighbour, current);

        //If this neighbour is on the open list, check if the current score traversing from
        //the current node rather than its previous parent is better
        if (neighbour.open || neighbour.closed)
        {
          //If our score is better, override the node
          if (g <= neighbour.g)
          {
            neighbour.setG(current, g);

            //Delete & re-add to the openlist to facilitate sorting as its f score has changed
            if(g != neighbour.g)
            {
              openList.remove(neighbour);
              openList.add(neighbour);
            }
          }

          //TODO - Deal with multiple parents if G scores are equal? As they may not be equivalent in this example
        }
        //Node is not open, give it an initial score
        else
        {
          neighbour.setG(current, g);
          openList.add(neighbour);
        }
      }
    }

    Set<Point> allPaths = new HashSet<>();
    for (State endState : endStates)
    {
      unwindPathRecur(nodeMap.get(endState), allPaths);
    }

    if(debug)
    {
      System.out.println("*** Showing checked cells ***");
      maze.drawMap(
        nodeMap,
        allPaths
      );
      System.out.println("*** End map ***");
    }

    return new Pair<>(allPaths, nodeMap.get(endStates.stream().findAny().orElse(null)).g);
  }

  //Generate next states from current state. Reindeer can either go forward, or turn clockwise/anti-clockwise.
  //Straight will not be included if it hits a wall
  private static List<State> getNextStates(Maze maze, AStarNode curState)
  {
    State turnClockwise = new State(curState.pos, curState.dir.rotateClockwise());
    State turnCounterClockwise = new State(curState.pos, curState.dir.rotateCounterClockwise());

    Point forwardPoint = curState.pos.add(curState.dir);
    if(maze.getType(forwardPoint) != SpaceType.WALL)
    {
      State goForward = new State(forwardPoint, curState.dir);
      return List.of(goForward, turnClockwise, turnCounterClockwise);
    }

    return List.of(turnClockwise, turnCounterClockwise);
  }

  //Recursively unwind/back-track path from ending node getting
  private static void unwindPathRecur(AStarNode node, Set<Point> pathPoints)
  {
    pathPoints.add(node.pos);

    for (AStarNode parent : node.parents)
    {
      unwindPathRecur(parent, pathPoints);
    }
  }

  public static Maze parseMaze(StringInput input)
  {
    Maze maze = new Maze();

    List<String> lines = input.asLines();
    maze.width = lines.getFirst().length();
    maze.height = lines.size();
    maze.spaces = new HashMap<>();

    for (int i = 0; i < lines.size(); i++)
    {
      String line = lines.get(i).trim();
      char[] chars = line.toCharArray();
      assert chars.length == maze.width;

      for (int j = 0; j < chars.length; j++)
      {
        char c = chars[j];

        if (c == '#')
        {
          Point pt = Point.makeXYPoint(j, i);
          maze.spaces.put(pt, SpaceType.WALL);
        }
        else if (c == 'S')
        {
          maze.start = Point.makeXYPoint(j, i);
        }
        else if (c == 'E')
        {
          maze.end = Point.makeXYPoint(j, i);
        }
      }
    }

    return maze;
  }

  public static class Maze
  {
    public SpaceType getType(Point pt)
    {
      var type = spaces.get(pt);
      return type == null ? SpaceType.EMPTY : type;
    }

    public void drawMap(Point reindeerPos, Set<Point> path)
    {
      StringBuilder builder = new StringBuilder();

      for (int y = 0; y < height; y++)
      {
        for (int x = 0; x < width; x++)
        {
          char c = '.';
          Point pt = Point.makeXYPoint(x, y);

          SpaceType type = getType(pt);

          if (type.equals(SpaceType.WALL))
          {
            c = '#';
          }
          else if (pt.equals(reindeerPos))
          {
            c = '@';
          }
          else if (pt.equals(end))
          {
            c = 'E';
          }
          else if (pt.equals(start))
          {
            c = 'S';
          }
          else if(path.contains(pt))
          {
            c = 'X';
          }

          builder.append(c);
        }
        builder.append('\n');
      }

      System.out.println(builder.toString());
    }

    public void drawMap(Map<State, AStarNode> nodes, Set<Point> path)
    {
      StringBuilder builder = new StringBuilder();


      for (int y = 0; y < height; y++)
      {
        for (int x = 0; x < width; x++)
        {
          char c = '.';
          Point pt = Point.makeXYPoint(x, y);

          SpaceType type = getType(pt);

          if (type.equals(SpaceType.WALL))
          {
            c = '+';
          }
          else if(path.contains(pt))
          {
            c = '#';
          }
          else
          {
            c = ' ';
            for (Vector cardinalDirection : Vector.CARDINAL_DIRECTIONS)
            {
              AStarNode node = nodes.get(new State(pt, cardinalDirection));

              if(node == null)
              {
                continue;
              }
              else if (node.closed)
              {
                c = 'x';
                break;
              }
              else if (node.open)
              {
                c = '_';
              }
            }
          }

          builder.append(c);
        }
        builder.append('\n');
      }

      System.out.println(builder.toString());
    }

    public int width;
    public int height;

    public Point start;
    public Point end;
    public Map<Point, SpaceType> spaces;
  }

  //A node generated by the A* algorithm to keep track of a state, its best cost &
  //what parent states can get to this state (at the given optimal cost)
  private static class AStarNode
  {
    //Construct a node with no cost or F value
    public static AStarNode makeNull(Maze maze, State state)
    {
      var node = new AStarNode();
      node.pos = state.point;
      node.dir = state.direction;
      node.open = false;
      node.closed = false;
      node.parents = new ArrayList<>();
      node.h = calcHeuristic(maze, state.point, state.direction);

      return node;
    }

    //Calculate heuristic for this node
    public static int calcHeuristic(Maze maze, Point point, Vector dir)
    {
      //You could do a more complicated heuristic that incorporates turning but you need to calculate
      //this a lot & without knowing how many turns will be in a route this is honestly the fastest
      //solution I've found. Even just prioritzing solutions looking in the correct direction was
      //slower than just straight manhattan distance. Realistically it won't really matter and you'll
      //just end up following the cost function anyway as the penalty for turning is so severe
      return (int)Vector.manhattanDistance(maze.end, point);
    }

    //Set the cost of this node
    public void setG(AStarNode parent, long g)
    {
      if(this.g != g)
      {
        this.parents.clear();
      }

      this.open = true;
      this.g = g;
      this.f = g + h;

      if(parent != null)
      {
        this.parents.add(parent);
      }
    }

    //Calculate the code of traversing from the parent state to the current state
    public static long calculateG(AStarNode current, AStarNode parent)
    {
      if(current.pos.equals(parent.pos))
      {
        return parent.g + 1000L;
      }

      return parent.g + 1L;
    }

    private List<AStarNode> parents;
    private Vector dir;
    private Point pos;
    private boolean open;
    private boolean closed;
    private long f; // G + H
    private long g; // Cost to get to this node
    private long h; // Heurisitic cost to get to the end
  }

  //An immutable state which represents a position & a current direction in the maze
  private static class State
  {
    public State(Point point, Vector direction)
    {
      this.point = point;
      this.direction = direction;
    }

    @Override
    public boolean equals(Object o)
    {
      if (!(o instanceof State state))
      {
        return false;
      }
      return Objects.equals(point, state.point) && Objects.equals(direction, state.direction);
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(point, direction);
    }

    private final Point point;
    private final Vector direction;
  }

  //The possible contents of a maze tile
  public enum SpaceType
  {
    EMPTY,
    WALL
  }
}
