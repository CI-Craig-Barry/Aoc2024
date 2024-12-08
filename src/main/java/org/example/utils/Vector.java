package org.example.utils;

public class Vector
  extends Point
{
  public Vector(int row, int col)
  {
    super(row, col);
  }

  public static Vector subtract(Point p1, Point p2)
  {
    return new Vector(
      p1.row - p2.row,
      p1.col - p2.col
    );
  }
}
