package org.example;

import org.example.utils.Pair;

import java.util.*;

public class Day6
{
  public static int task1(List<String> lines)
  {
    Map map = makeMap(lines);
    Guard guard = new Guard(map);

    while(guard.move())
    {
      //Draw the map on every movement for fun
      map.drawMap(
        guard.visited,
        new int[] {guard.posRow, guard.posCol}
      );
    }

    return guard.numSpacesVisited();
  }

  public static int task2(List<String> lines)
  {
    Map map = makeMap(lines);
    int numObstaclePosition = 0;
    boolean[][] potentialObstaclePlace = findPotentialObstaclePlacements(map);

    //Iterate over every potentional place to put an obstacle
    for(int i = 0; i < map.height; i++)
    {
      for(int j = 0; j < map.width; j++)
      {
        //Already obstacle or is guards starting position, no need to check
        if(map.isObstacle(i, j) ||
          (map.initialGuardRow == i && map.initialGuardCol == j))
        {
          continue;
        }
        //Can contain a placed obstacle
        else if(potentialObstaclePlace[i][j])
        {
          //Remake the guard every position to reset visited positions
          Guard guard = new Guard(map);
          //Set the placed obstacle
          map.placedObstacle = new int[] {i, j};

          //If we reach end of while loop naturally, we left the maze
          while(guard.move())
          {
            //If guard stuck in a loop, draw the map & count the obstacle
            if(guard.stuckInLoop)
            {
              //Draw map for fun
              map.drawMap(
                guard.visited,
                new int[] {guard.posRow, guard.posCol}
              );

              numObstaclePosition += 1;
              break;
            }
          }
        }
      }
    }

    return numObstaclePosition;
  }

  private static Map makeMap(List<String> lines)
  {
    Map map = new Map();
    map.width = lines.getFirst().length();
    map.height = lines.size();

    map.obstacles = new boolean[map.height][];

    for(int i = 0; i < lines.size(); i++)
    {
      String line = lines.get(i).trim();
      char[] chars = line.toCharArray();
      map.obstacles[i] = new boolean[map.width];

      assert chars.length == map.width;

      for(int j = 0 ; j < chars.length; j++)
      {
        //If this point is an obstacle
        map.obstacles[i][j] = (chars[j] == '#');

        if(chars[j] == '^')
        {
          map.initialGuardRow = i;
          map.initialGuardCol = j;
        }
      }
    }

    return map;
  }

  //Potential places are just anywhere the guard visited in his initial path
  private static boolean[][] findPotentialObstaclePlacements(Map map)
  {
    Guard guard = new Guard(map);
    while(guard.move()) {}

    return guard.visited;
  }

  private static class Guard
  {
    private Guard(Map map)
    {
      this.map = map;

      this.posCol = map.initialGuardCol;
      this.posRow = map.initialGuardRow;
      this.prevPos = new int[]{posCol, posRow};

      this.visited = new boolean[map.height][map.width];
      this.visited[map.initialGuardRow][map.initialGuardCol] = true;

      this.visitedHashes = new HashSet<>();
      this.visitedHashes.add(hashPosition());
    }

    // Return false if guard leaves the map
    private boolean move()
    {
      prevPos = new int[]{posCol, posRow};
//      turned = false;

      int[] nextPos;
      //Keep turning right until our next position is free
      do
      {
        nextPos = getNextPos();

        if(map.isObstacle(nextPos[0], nextPos[1]))
        {
//          turned = true;
          turnRight();
        }
        else
        {
          break;
        }
      }
      while(true);

      posRow = nextPos[0];
      posCol = nextPos[1];

      boolean inBounds = map.isWithinBounds(posRow, posCol);
      //Update which spots we have visited
      if(inBounds)
      {
        visited[posRow][posCol] = true;
        long positionHash = hashPosition();

        if(visitedHashes.contains(positionHash))
        {
          stuckInLoop = true;
        }
        else
        {
          visitedHashes.add(positionHash);
        }
      }

      return inBounds;
    }

    private int[] getNextPos()
    {
      return new int[]
        {
          posRow + rowDirection,
          posCol + colDirection
        };
    }

    private int[] getPrevPos()
    {
      return prevPos;
    }

    private long hashPosition()
    {
      //Should give the index of the position in a flat map
      long positionHash = ((long) posCol * map.height + posRow);

      //Should mean there is atleast 4 gaps between each position hash
      positionHash *= 5;

      //Should give unique values from [-2 to 2] for each direction
      long directionHash = colDirection + rowDirection * 2L;

      //Should give a unique hash for each position & direction combination
      return positionHash + directionHash;
    }

    private void turnRight()
    {
      int curColDirection = colDirection;
      int curRowDirection = rowDirection;

      //2d vector clockwise rotation
      rowDirection = curColDirection;
      colDirection = -curRowDirection;
    }

    public int numSpacesVisited()
    {
      int count = 0;

      for(int i = 0; i < map.height; i++)
      {
        for(int j = 0 ; j < map.width; j++)
        {
          count += visited[i][j] ? 1 : 0;
        }
      }

      return count;
    }

    private final Map map;
    private boolean[][] visited;

    private Set<Long> visitedHashes;

    private boolean stuckInLoop = false;

    private int[] prevPos;
    private int posRow;
    private int posCol;

    private int rowDirection = -1;
    private int colDirection = 0;
  }

  private static class Map
  {
    public boolean isWithinBounds(int row, int col)
    {
      return row >= 0 && row < height &&
        col >= 0 && col < width;
    }

    public boolean isObstacle(int row, int col)
    {
      if(!isWithinBounds(row, col))
      {
        return false;
      }

      return obstacles[row][col] ||
        (placedObstacle != null && placedObstacle[0] == row && placedObstacle[1] == col);
    }

    public void drawMap(boolean[][] visited, int[] guardPos)
    {
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < height; i++)
      {
        for(int j = 0 ; j < width; j++)
        {
          char c = '.';

          if(i == guardPos[0] && j == guardPos[1])
          {
            c = '^';
          }
          else if(placedObstacle != null && (i == placedObstacle[0] && j == placedObstacle[1]))
          {
            c = 'O';
          }
          else if(obstacles[i][j])
          {
            c = '#';
          }
          else if(visited[i][j])
          {
            c = 'X';
          }

          builder.append(c);
        }
        builder.append('\n');
      }

      System.out.println("====START MAP===");
      System.out.println(builder.toString());
      System.out.println("====END   MAP===\n");
    }

    private int initialGuardRow;
    private int initialGuardCol;
    private int width;
    private int height;
    private boolean[][] obstacles;
    private int[] placedObstacle;
  }
}
