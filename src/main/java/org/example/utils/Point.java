package org.example.utils;

import java.util.Objects;

public class Point
{
  public Point(int row, int col)
  {
    this.row = row;
    this.col = col;
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

  public int row;
  public int col;
}
