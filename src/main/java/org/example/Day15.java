package org.example;

import org.example.utils.*;
import org.example.utils.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class Day15
{
  private static boolean debug;

  public static long task1(StringInput input, boolean debug)
  {
    Day15.debug = debug;

    List<StringInput> inputs = input.splitAtEmptyLine();
    assert inputs.size() == 2;
    StringInput mapInput = inputs.get(0);
    StringInput movesInput = inputs.get(1);

    WarehouseMap map = makeMap(mapInput);
    List<Vector> movements = makeMoves(movesInput);

    Point robot = map.robotStartPosition;

    if(debug)
    {
      System.out.println("*** Initial Map ***");
      map.drawMap(robot);
      System.out.println("*** End ***");
      System.out.println();
    }

    for (Vector movement : movements)
    {
      robot = map.moveRobot(robot, movement);

      if(debug)
      {
        System.out.println("*** Moving " + movement + "***");
        map.drawMap(robot);
        System.out.println("*** End ***");
        System.out.println();
      }
    }

    if(debug)
    {
      System.out.println("*** Final Map ***");
      map.drawMap(robot);
      System.out.println("*** End ***");
      System.out.println();
    }

    return map.getSumOfGps();
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day15.debug = debug;

    List<StringInput> inputs = input.splitAtEmptyLine();
    assert inputs.size() == 2;
    StringInput mapInput = inputs.get(0);
    StringInput movesInput = inputs.get(1);

    WarehouseMap map = makeBigMap(mapInput);
    List<Vector> movements = makeMoves(movesInput);

    Point robot = map.robotStartPosition;

    if(debug)
    {
      System.out.println("*** Initial Map ***");
      map.drawMap(robot);
      System.out.println("*** End ***");
      System.out.println();
    }

    for (Vector movement : movements)
    {
      robot = map.moveRobot(robot, movement);

      if(debug)
      {
        System.out.println("*** Moving " + movement + "***");
        map.drawMap(robot);
        System.out.println("*** End ***");
        System.out.println();
      }
    }

    if(debug)
    {
      System.out.println("*** Final Map ***");
      map.drawMap(robot);
      System.out.println("*** End ***");
      System.out.println();
    }

    return map.getSumOfGps();
  }

  //Construct map from string input
  private static WarehouseMap makeMap(StringInput input)
  {
    WarehouseMap map = new WarehouseMap();
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
          map.spaces.put(pt, WarehouseMap.SpaceType.WALL);
        }
        else if(c == 'O')
        {
          Point pt = Point.makeXYPoint(j, i);
          map.spaces.put(pt, WarehouseMap.SpaceType.BOX);
        }
        else if(c == '@')
        {
          map.robotStartPosition = Point.makeXYPoint(j, i);
        }
      }
    }

    return map;
  }

  //Construct big map
  private static WarehouseMap makeBigMap(StringInput input)
  {
    WarehouseMap map = new WarehouseMap();
    List<String> lines = input.asLines();
    map.width = lines.getFirst().length() * 2;
    map.height = lines.size();
    map.spaces = new HashMap<>();
    map.bigMap = true;

    for(int y = 0; y < lines.size(); y++)
    {
      String line = lines.get(y).trim();
      char[] chars = line.toCharArray();

      for(int x = 0 ; x < chars.length; x++)
      {
        char c = chars[x];

        Point pt1 = Point.makeXYPoint(x * 2L, y);
        Point pt2 = Point.makeXYPoint((x * 2L) + 1L, y);

        if(c == '#')
        {
          map.spaces.put(pt1, WarehouseMap.SpaceType.WALL);
          map.spaces.put(pt2, WarehouseMap.SpaceType.WALL);
        }
        else if(c == 'O')
        {
          map.spaces.put(pt1, WarehouseMap.SpaceType.LEFT_BOX);
          map.spaces.put(pt2, WarehouseMap.SpaceType.RIGHT_BOX);
        }
        else if(c == '@')
        {
          map.robotStartPosition = pt1;
        }
      }
    }

    return map;
  }

  //Construct movement list
  private static List<Vector> makeMoves(StringInput input)
  {
    String inputStr = input.asString();
    List<Vector> movements = new ArrayList<>(inputStr.length());

    for (char c : inputStr.toCharArray())
    {
      Vector direction = switch (c)
      {
        case '^' -> Vector.makeXYvector(0, -1);
        case 'v' -> Vector.makeXYvector(0, 1);
        case '>' -> Vector.makeXYvector(1, 0);
        case '<' -> Vector.makeXYvector(-1, 0);
        default -> null;
      };

      if(direction != null)
      {
        movements.add(direction);
      }
    }

    return movements;
  }

  private static class BigBox
  {
    //Create a big box from a point
    public static BigBox make(WarehouseMap map, Point pt, WarehouseMap.SpaceType spaceType)
    {
      BigBox box = new BigBox();

      if(spaceType == WarehouseMap.SpaceType.RIGHT_BOX)
      {
        box.right = pt;
        box.left = pt.add(Vector.makeXYvector(-1, 0));
      }
      else if(spaceType == WarehouseMap.SpaceType.LEFT_BOX)
      {
        box.left = pt;
        box.right = pt.add(Vector.makeXYvector(1, 0));
      }

      return box;
    }

    //Check if vector is horizontal
    private static boolean isHorizontal(Vector direction)
    {
      return direction.getX() != 0;
    }

    //Check if vector points right
    private static boolean isRight(Vector direction)
    {
      return direction.getX() > 0;
    }

    //Get gps of this box
    private long getGps(WarehouseMap map)
    {
      return (100L * left.getY()) + left.getX();
    }

    //Get boxes which will also move if this box is moved (not recursive)
    public List<BigBox> getAttachedBoxes(WarehouseMap map, Vector direction)
    {
      List<BigBox> boxes = new ArrayList<>();

      if(isHorizontal(direction))
      {
        getHorizontalAttachedBox(map, direction).ifPresent(boxes::add);
        return boxes;
      }

      //Get vertically attached boxes
      Point rightPoint = right.add(direction);
      Point leftPoint = left.add(direction);

      var rightType = map.getType(rightPoint);
      var leftType = map.getType(leftPoint);

      if(rightType.isBox())
      {
        boxes.add(BigBox.make(map, rightPoint, rightType));
      }
      if(leftType.isBox())
      {
        boxes.add(BigBox.make(map, leftPoint, leftType));
      }

      return boxes;
    }

    //Check if we can move in a given direction
    public boolean canMoveIn(WarehouseMap map, Vector direction)
    {
      Point left = this.left.add(direction);
      Point right = this.right.add(direction);

      return !(map.isWall(left) || map.isWall(right));
    }

    //Remove this box from the map
    public void removeBox(WarehouseMap map)
    {
      assert map.spaces.get(left) == WarehouseMap.SpaceType.LEFT_BOX;
      assert map.spaces.get(right) == WarehouseMap.SpaceType.RIGHT_BOX;

      map.spaces.remove(left);
      map.spaces.remove(right);
    }

    //Add big box in the given direction from this position
    public void addInDirection(WarehouseMap map, Vector direction)
    {
      Point newLeft = left.add(direction);
      Point newRight = right.add(direction);
      map.spaces.put(newLeft, WarehouseMap.SpaceType.LEFT_BOX);
      map.spaces.put(newRight, WarehouseMap.SpaceType.RIGHT_BOX);
    }

    //Get box attached in horizontal direction
    private Optional<BigBox> getHorizontalAttachedBox(WarehouseMap map, Vector direction)
    {
      if(isRight(direction))
      {
        Point pt = right.add(RIGHT_DIR);
        if(map.isLeftBox(pt))
        {
          return Optional.of(BigBox.make(map, pt, WarehouseMap.SpaceType.LEFT_BOX));
        }
      }
      else
      {
        Point pt = left.add(LEFT_DIR);
        if(map.isRightBox(pt))
        {
          return Optional.of(BigBox.make(map, pt, WarehouseMap.SpaceType.RIGHT_BOX));
        }
      }

      return Optional.empty();
    }

    @Override
    public boolean equals(Object o)
    {
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }
      BigBox bigBox = (BigBox) o;
      return Objects.equals(left, bigBox.left) && Objects.equals(right, bigBox.right);
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(left, right);
    }

    private Point left;
    private Point right;
  }

  private static class WarehouseMap
  {
    //Return true if coordinates are a wall (either placed or static)
    public boolean isWall(Point point)
    {
      return spaces.get(point) == SpaceType.WALL;
    }

    //Get type of point
    public SpaceType getType(Point point)
    {
      var type = spaces.get(point);
      return type == null ? SpaceType.EMPTY : type;
    }

    public boolean isBox(Point point)
    {
      var type = spaces.get(point);
      return type != null && type.isBox();
    }

    public boolean isLeftBox(Point point)
    {
      return spaces.get(point) == SpaceType.LEFT_BOX;
    }

    public boolean isRightBox(Point point)
    {
      return spaces.get(point) == SpaceType.RIGHT_BOX;
    }

    //Get gps for a given point
    private long getGps(Point point)
    {
      assert !bigMap;
      return (point.getY() * 100L) + point.getX();
    }

    //Get sum of gps
    private long getSumOfGps()
    {
      if(bigMap)
      {
        return getBigMapGpsSum();
      }

      var gpsCords = spaces.entrySet()
        .stream()
        .filter((entry) ->
          entry.getValue().equals(SpaceType.BOX)
        )
        .toList();
      long total = 0L;

      for (Map.Entry<Point, SpaceType> entry : gpsCords)
      {
        total += getGps(entry.getKey());
      }

      return total;
    }

    //Get GPS sum for big map
    private long getBigMapGpsSum()
    {
      var gpsCords = spaces.entrySet()
        .stream()
        .filter((entry) ->
          entry.getValue().equals(SpaceType.LEFT_BOX)
        )
        .toList();

      long total = 0L;
      for (Map.Entry<Point, SpaceType> gpsCord : gpsCords)
      {
        var box = BigBox.make(this, gpsCord.getKey(), gpsCord.getValue());
        total += box.getGps(this);
      }

      return total;
    }

    //Move robot from position in direction
    public Point moveRobot(Point robotPos, Vector direction)
    {
      Point expectedPlace = robotPos.add(direction);

      //Is a wall movement not possible
      if(isWall(expectedPlace))
      {
        return robotPos;
      }
      //Is a box, see if pushing it is possible
      else if(isBox(expectedPlace))
      {
        //If this is a big map handle big boxes
        if(bigMap)
        {
          return moveBigBox(robotPos, direction, expectedPlace);
        }
        
        //Continue moving in the same direction to see if we can push boxes
        Point nextCheckPos = expectedPlace;
        while(isBox(nextCheckPos))
        {
          nextCheckPos = nextCheckPos.add(direction);
        }

        //If we have a series of boxes followed by a wall pushing is not possible
        if(isWall(nextCheckPos))
        {
          return robotPos;
        }

        //If we have a series of boxes followed by an empty space, we push
        //(This can be simplified as removing the first box & placing it at the end position)
        //Remove first box
        spaces.remove(expectedPlace);
        //Add box to end
        spaces.put(nextCheckPos, SpaceType.BOX);

        //And movement was successful, return new position
        return expectedPlace;
      }

      //Not box or wall, movement is as expected
      return expectedPlace;
    }

    //Move big box, this will move attached boxes aswell. Returns robot position
    private Point moveBigBox(Point robotStart, Vector direction, Point boxStart)
    {
      //Use a linked hash set to not count duplicates & maintain insertion order for moving
      LinkedHashSet<BigBox> boxes = new LinkedHashSet<>();
      boxes.add(BigBox.make(this, boxStart, getType(boxStart)));

      List<BigBox> boxAddList = new ArrayList<>(boxes);
      ListIterator<BigBox> iter = boxAddList.listIterator();

      while(iter.hasNext())
      {
        List<BigBox> attachedBoxes = iter.next().getAttachedBoxes(this, direction);
        attachedBoxes.forEach(iter::add);

        for (BigBox attachedBox : attachedBoxes)
        {
          iter.previous();
        }
      }

      //Add all attached boxes to box list
      boxes.addAll(boxAddList);

      //If all the boxes can be moved, we need to move the boxes
      if(boxes.stream().allMatch(box -> box.canMoveIn(this, direction)))
      {
        boxes.forEach(box -> box.removeBox(this));
        boxes.forEach(box -> box.addInDirection(this, direction));

        //Move success
        return boxStart;
      }

      return robotStart;
    }

    //Draw map for fun
    public void drawMap(Point robotPos)
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
          else if(type.equals(SpaceType.BOX))
          {
            c = 'O';
          }
          else if(type.equals(SpaceType.LEFT_BOX))
          {
            c = '[';
          }
          else if(type.equals(SpaceType.RIGHT_BOX))
          {
            c = ']';
          }
          else if(pt.equals(robotPos))
          {
            c = '@';
          }

          builder.append(c);
        }
        builder.append('\n');
      }

      System.out.println(builder.toString());
    }

    private enum SpaceType
    {
      EMPTY,
      WALL,
      BOX,
      LEFT_BOX,
      RIGHT_BOX;

      public boolean isBox()
      {
        return this == BOX ||
          this == LEFT_BOX ||
          this == RIGHT_BOX;
      }
    }

    private Map<Point, SpaceType> spaces;

    private boolean bigMap;
    private Point robotStartPosition;
    private int width;
    private int height;
  }

  private static final Vector RIGHT_DIR = Vector.makeXYvector(1, 0);
  private static final Vector LEFT_DIR = Vector.makeXYvector(-1, 0);
  private static final Vector UP_DIR = Vector.makeXYvector(0, -1);
  private static final Vector DOWN_DIR = Vector.makeXYvector(0, 1);
}
