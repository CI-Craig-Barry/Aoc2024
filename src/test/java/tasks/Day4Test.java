//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.*;
import org.junit.jupiter.api.*;
import utils.FileUtils;

public class Day4Test
{
  @Test
  public void testTask1()
  {
    String input =
      """
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
""";
    Assertions.assertEquals(18, Day4.task1(input));
  }

  @Test
  public void task1()
  {
    String input = FileUtils.getFileAsString("/day4_task.txt").trim();

    int result = Day4.task1(input);
    Assertions.assertEquals(2639, result);
  }

  @Test
  public void testTask2()
  {
    String input =
      """
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
""";
    Assertions.assertEquals(9, Day4.task2(input));
  }

  @Test
  public void task2()
  {
    String input = FileUtils.getFileAsString("/day4_task.txt").trim();

    int result = Day4.task2(input);
    Assertions.assertEquals(2005, result);
  }
}
