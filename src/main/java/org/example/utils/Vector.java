package org.example.utils;

import java.util.*;

public class Vector
  extends Point
{
  public Vector(int row, int col)
  {
    super(row, col);
  }

  public Vector(long row, long col)
  {
    super(row, col);
  }

  public static Vector makeXYvector(long x, long y)
  {
    return new Vector(y, x);
  }

  public static Vector subtract(Point p1, Point p2)
  {
    return new Vector(
      p1.row - p2.row,
      p1.col - p2.col
    );
  }

  public static Vector scale(Vector vec, int scale)
  {
    return new Vector(
      vec.row * scale,
      vec.col * scale
    );
  }

  public static long manhattanDistance(Point p1, Point p2)
  {
    Vector diff = Vector.subtract(p1, p2);
    return Math.abs(diff.row) + Math.abs(diff.col);
  }

  public Vector rotateClockwise()
  {
    //2d vector clockwise rotation
    return new Vector(
      col,
      -row
    );
  }

  public Vector rotateCounterClockwise()
  {
    //2d vector counter-clockwise rotation
    return new Vector(
      -col,
      row
    );
  }

  public Vector reverse()
  {
    return new Vector(-row, -col);
  }

  public static final Vector EAST = Vector.makeXYvector(1, 0);
  public static final Vector WEST = Vector.makeXYvector(-1, 0);
  public static final Vector NORTH = Vector.makeXYvector(0, -1);
  public static final Vector SOUTH = Vector.makeXYvector(0, 1);

  public static final Vector NORTH_EAST = Vector.makeXYvector(1, -1);
  public static final Vector NORTH_WEST = Vector.makeXYvector(-1, -1);
  public static final Vector SOUTH_EAST = Vector.makeXYvector(1, 1);
  public static final Vector SOUTH_WEST = Vector.makeXYvector(-1, 1);

  public static final List<Vector> CARDINAL_DIRECTIONS;
  public static final List<Vector> EIGHT_CARDINAL_DIRECTIONS;

  static
  {
    CARDINAL_DIRECTIONS = List.of(
      EAST,
      WEST,
      NORTH,
      SOUTH
    );

    EIGHT_CARDINAL_DIRECTIONS = List.of(
      EAST,
      NORTH_EAST,
      NORTH,
      NORTH_WEST,
      WEST,
      SOUTH_WEST,
      SOUTH,
      SOUTH_EAST
    );
  };
}
