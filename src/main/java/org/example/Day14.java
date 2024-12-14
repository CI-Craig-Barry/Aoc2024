package org.example;

import org.example.utils.*;
import org.example.utils.Vector;

import java.io.*;
import java.util.*;

public class Day14
{
  private static boolean debug = false;

  //Task 1
  public static long task1(StringInput input, int spaceWidth, int spaceHeight, boolean debug)
  {
    Day14.debug = debug;

    BathroomMap map = new BathroomMap(spaceWidth, spaceHeight);
    List<Robot> robots = parseRobots(input);

    if(debug)
    {
      System.out.println("===Starting map===");
      map.drawMap(robots);
    }

    for (Robot robot : robots)
    {
      for(int i = 0; i < 100; i++)
      {
        robot.move(map);
      }
    }


    if(debug)
    {
      System.out.println("===Finished map===");
      map.drawMap(robots);
    }

    return calculateSafetyFactor(map, robots);
  }

  //Task 2
  public static long task2(StringInput input, int spaceWidth, int spaceHeight, boolean debug)
  {
    Day14.debug = debug;

    //Print system output to file for easier manual checking
    File sysoutLog = new File("day14.log");
    PrintStream ps = null;
    try
    {
      ps = new PrintStream(new FileOutputStream(sysoutLog));
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException(e);
    }
    System.setOut(ps);

    //Make map & parse robots
    BathroomMap map = new BathroomMap(spaceWidth, spaceHeight);
    List<Robot> robots = parseRobots(input);

    if(debug)
    {
      System.out.println("===Starting map===");
      map.drawMap(robots);
    }

    int maxFullySurroundedPts = 0;
    int maxFullySurroundedIdx = 0;

    int i = 0;
    while(true)
    {
      for (Robot robot : robots)
      {
        robot.move(map);
      }
      //Increment every movement iteration
      i++;

      int[][] robotCountMap = map.buildCountMap(robots);
      int fullySurroundedPts = countFullySurrounded(robotCountMap, robots);

//      //This check will work but is kind of cheese, it is how I got the answer initially
//      if(countsHasOnlyOnes(robotCountMap))
//      {
//        System.out.println("*** Found christmas ***");
//        map.drawMap(robots);
//        System.out.println();
//        return i;
//      }

      //Feels less cheese, but it also only really makes sense once you know what the output looks like.
      //But is not an unreasonable estimate
      if(maxFullySurroundedPts < fullySurroundedPts)
      {
        System.err.println("*** Iteration: " + i + ", Highest number of fully surrounded points: " + fullySurroundedPts + " ***");
        System.out.println("*** Iteration: " + i + ", Highest number of fully surrounded points: " + fullySurroundedPts + " ***");
        map.drawMap(robots);
        System.out.println();

        maxFullySurroundedIdx = i;
        maxFullySurroundedPts = fullySurroundedPts;
      }

      //Check if we can stop iterating as we have reached initial conditions
      if(robots.stream().allMatch(Robot::isAtInitialPosition))
      {
        System.out.println("All robots at initial position, iteration " + (i+1));
        break;
      }
    }

    return maxFullySurroundedIdx;
  }

  //Parse input string to robot instances
  private static List<Robot> parseRobots(StringInput input)
  {
    List<Robot> robots = new ArrayList<>();

    for (String line : input.asLines())
    {
      Object[] vars = StringUtils.findVariables("p=%d,%d v=%d,%d", line);
      Robot robot = new Robot();
      robot.initPosition = Point.makeXYPoint((long)vars[0], (long)vars[1]);
      robot.position = Point.makeXYPoint((long)vars[0], (long)vars[1]);
      robot.direction = Vector.makeXYvector((long)vars[2], (long)vars[3]);
      robots.add(robot);
    }

    return robots;
  }

  //Check if any cell has more than 1 robot
  private static boolean countsHasOnlyOnes(int[][] robotCounts)
  {
    return Arrays.stream(robotCounts).flatMapToInt(Arrays::stream).noneMatch(v -> v > 1);
  }

  //Count the number of robots who are completely surrounded in all cardinal directions (including diagonal)
  private static int countFullySurrounded(int[][] robotCounts, List<Robot> robots)
  {
    int total = 0;

    for (Robot robot : robots)
    {
      if(isFullySurrounded(robotCounts, robot))
      {
        total++;
      }
    }

    return total;
  }

  //Check if a robot is completed surrounded
  private static boolean isFullySurrounded(int[][] robotCounts, Robot robot)
  {
    Point point = robot.position;

    int width = robotCounts.length;
    int height = robotCounts[0].length;

    int neighbourSearchSize = 1;

    long startX = Math.max(point.getX() - neighbourSearchSize, 0);
    long startY = Math.max(point.getY() - neighbourSearchSize, 0);

    long endX = Math.min(point.getX() + neighbourSearchSize, width - 1);
    long endY = Math.min(point.getY() + neighbourSearchSize, height - 1);

    for(long x = startX; x < endX; x++)
    {
      for(long y = startY; y < endY; y++)
      {
        if(robotCounts[(int)x][(int)y] == 0)
        {
          return false;
        }
      }
    }

    return true;
  }

  //Calculate the number of robots in each quadrant, results are in array of quadrants starting from
  //top-left moving anti-clockwise
  private static long[] calculateRobotsInQuadrants(BathroomMap map, List<Robot> robots)
  {
    long[] robotsInQuadrant = new long[] {0, 0, 0, 0};

    int halfWidth = map.width / 2;
    int halfHeight = map.height / 2;

    for (Robot robot : robots)
    {
      //Left side
      if(robot.position.getX() < halfWidth)
      {
        //Top-Left
        if(robot.position.getY() < halfHeight)
        {
          robotsInQuadrant[0]++;
        }
        //Bottom-Left
        else if(robot.position.getY() > halfHeight)
        {
          robotsInQuadrant[1]++;
        }
      }
      //Right side
      else if(robot.position.getX() > halfWidth)
      {
        //Top-right
        if(robot.position.getY() < halfHeight)
        {
          robotsInQuadrant[3]++;
        }
        //Bottom-right
        else if(robot.position.getY() > halfHeight)
        {
          robotsInQuadrant[2]++;
        }
      }
    }

    return robotsInQuadrant;
  }

  //Calculate safety factor for task 1
  private static long calculateSafetyFactor(BathroomMap map, List<Robot> robots)
  {
    long[] robotsInQuadrant = calculateRobotsInQuadrants(map, robots);

    return Arrays.stream(robotsInQuadrant)
      .reduce((i1, i2) -> i1 * i2)
      .orElse(0);
  }

  private static class BathroomMap
  {
    public BathroomMap(int width, int height)
    {
      this.width = width;
      this.height = height;
    }

    //Build a 2D array which is just the count of robots in each cell (x,y) position
    public int[][] buildCountMap(List<Robot> robots)
    {
      int[][] counts = new int[width][height];

      for (Robot robot : robots)
      {
        counts[(int)robot.position.getX()][(int)robot.position.getY()] += 1;
      }

      return counts;
    }

    //Draw map & print to system.out
    public void drawMap(List<Robot> robots)
    {
      StringBuilder builder = new StringBuilder();

      int[][] counts = buildCountMap(robots);

      for(int y = 0; y < height; y++)
      {
        for(int x = 0; x < width; x++)
        {
          var pt = Point.makeXYPoint(x, y);
          long count = counts[x][y];

          if(count != 0)
          {
            builder.append(count);
          }
          else
          {
            builder.append(".");
          }
        }

        builder.append("\n");
      }

      System.out.println(builder.toString());
    }

    private final int width;
    private final int height;
  }

  private static class Robot
  {
    //Check if robot at its initial position
    public boolean isAtInitialPosition()
    {
      return position.equals(initPosition);
    }

    //Move the robot across the map
    public void move(BathroomMap map)
    {
      position = position.add(direction);

      long x = position.getX();
      long y = position.getY();

      if(x >= map.width)
      {
        x = x - map.width;
      }
      else if(x < 0)
      {
        x = map.width + x;
      }

      if(y >= map.height)
      {
        y = y - map.height;
      }
      else if(y < 0)
      {
        y = map.height + y;
      }

      position.setXYPos(x, y);
    }

    private Point initPosition;
    private Point position;
    private Vector direction;
  }
}
