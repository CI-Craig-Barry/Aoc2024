package org.example.utils;

import java.util.Objects;

public class Point
{
  public Point(int row, int col)
  {
    this.row = row;
    this.col = col;
  }

  public Point(long row, long col)
  {
    this.row = row;
    this.col = col;
  }

  public static Point makeXYPoint(long x, long y)
  {
    return new Point(y, x);
  }

  public Point add(Vector vector)
  {
    return new Point(
      this.row + vector.row,
      this.col + vector.col
    );
  }

  public Point subtract(Vector vector)
  {
    return new Point(
      this.row - vector.row,
      this.col - vector.col
    );
  }

  @Override
  public boolean equals(Object o)
  {
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    Point point = (Point) o;
    return row == point.row && col == point.col;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(row, col);
  }

  @Override
  public String toString()
  {
    return "[" + col + "," + row + "]";
  }

  public long getX()
  {
    return col;
  }

  public long getY()
  {
    return row;
  }

  public void setX(long x)
  {
    this.col = x;
  }

  public void setY(long y)
  {
    this.row = y;
  }

  public void setXYPos(long x, long y)
  {
    setX(x);
    setY(y);
  }

  public long row;
  public long col;
}
