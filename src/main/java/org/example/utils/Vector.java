package org.example.utils;

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

  public void rotateClockwise()
  {
    long curColDirection = col;
    long curRowDirection = row;

    //2d vector clockwise rotation
    this.row = curColDirection;
    this.col = -curRowDirection;
  }

  public void rotateCounterClockwise()
  {
    long curColDirection = col;
    long curRowDirection = row;

    //2d vector counter-clockwise rotation
    this.row = -curColDirection;
    this.col = curRowDirection;
  }

  public void reverse()
  {
    this.row = -this.row;
    this.col = -this.col;
  }
}
