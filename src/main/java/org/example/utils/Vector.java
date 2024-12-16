package org.example.utils;

import java.util.*;
import java.util.spi.CalendarNameProvider;

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

  public void reverse()
  {
    this.row = -this.row;
    this.col = -this.col;
  }

  public static final Vector EAST = Vector.makeXYvector(1, 0);
  public static final Vector WEST = Vector.makeXYvector(-1, 0);
  public static final Vector NORTH = Vector.makeXYvector(0, -1);
  public static final Vector SOUTH = Vector.makeXYvector(0, 1);
  public static final List<Vector> CARDINAL_DIRECTIONS;

  static
  {
    CARDINAL_DIRECTIONS = List.of(
      EAST,
      WEST,
      NORTH,
      SOUTH
    );
  };
}
