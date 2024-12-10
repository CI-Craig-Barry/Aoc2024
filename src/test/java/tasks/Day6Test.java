package tasks;

import org.example.*;
import org.example.utils.*;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

import java.util.*;

public class Day6Test
{
  private static final String TEST_INPUT = """
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
    """.trim();

  private static final List<String> TEST_INPUT_LINES = StringUtils.splitIntoLines(TEST_INPUT);

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(41, Day6.task1(TEST_INPUT_LINES));
  }

  @Test
  public void task1()
  {
    List<String> lines = FileUtils.getLines("/day6_task.txt");
    Assertions.assertEquals(4964, Day6.task1(lines));
  }

  @Test
  public void task2Test()
  {
    Assertions.assertEquals(6, Day6.task2(TEST_INPUT_LINES));
  }

  @Test
  public void task2()
  {
    List<String> lines = FileUtils.getLines("/day6_task.txt");
    Assertions.assertEquals(1740, Day6.task2(lines));
  }

  @Test
  public void task2PerformanceTest()
  {
    List<String> lines = FileUtils.getLines("/day6_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 100; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(1740, Day6.task2(lines));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
