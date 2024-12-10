package tasks;

import org.example.Day10;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day10Test
{
  private static final StringInput TEST_INPUT = new StringInput(
    """
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
    """.trim());

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(36L, Day10.task1(TEST_INPUT, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day10_task.txt");
    Assertions.assertEquals(512L, Day10.task1(input, false));
  }

  @Test
  public void task2Test()
  {
    Assertions.assertEquals(81L, Day10.task2(TEST_INPUT, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day10_task.txt");
    Assertions.assertEquals(1045L, Day10.task2(input, false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day10_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 1000; i++)
    {
      tester.startTiming();
      Day10.task2(input, false);
      tester.stopTiming();
    }

    tester.printResults();
  }
}
