//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.Day12;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day12Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
AAAA
BBCD
BBCC
EEEC
    """.trim());

  private static final StringInput TEST_INPUT_2 = new StringInput(
    """
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE
    """.trim());

  private static final StringInput TEST_INPUT_3 = new StringInput(
    """
EEEEE
EXXXX
EEEEE
EXXXX
EEEEE
    """.trim());

  private static final StringInput TEST_INPUT_4 = new StringInput(
    """
AAAAAA
AAABBA
AAABBA
ABBAAA
ABBAAA
AAAAAA
    """.trim());

  @Test
  public void task1Test1()
  {
    Assertions.assertEquals(140L, Day12.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1Test2()
  {
    Assertions.assertEquals(1930L, Day12.task1(TEST_INPUT_2, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day12_task.txt");
    Assertions.assertEquals(1319878L, Day12.task1(input, false));
  }

  @Test
  public void task2Test1()
  {
    Assertions.assertEquals(80L, Day12.task2(TEST_INPUT_1, true));
  }

  @Test
  public void task2Test2()
  {
    Assertions.assertEquals(236L, Day12.task2(TEST_INPUT_3, true));
  }

  @Test
  public void task2Test3()
  {
    Assertions.assertEquals(368L, Day12.task2(TEST_INPUT_4, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day12_task.txt");
    Assertions.assertEquals(784982L, Day12.task2(input, false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day12_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 1000; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(784982L, Day12.task2(input, false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
