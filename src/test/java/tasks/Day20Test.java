package tasks;

import org.example.Day20;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day20Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############  
    """.trim());

  @Test
  public void part1Test1()
  {
    Assertions.assertEquals(0L, Day20.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day20_task.txt");
    Assertions.assertEquals(1286L, Day20.task1(input, false));
  }

  @Test
  public void part2Test1()
  {
    Assertions.assertEquals(16l, Day20.task2(TEST_INPUT_1, 2, 2,true));
  }

  @Test
  public void part2Test2()
  {
    Assertions.assertEquals(16l, Day20.task2(TEST_INPUT_1, 50,20,true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day20_task.txt");
    Assertions.assertEquals(989316L, Day20.task2(input, 100, 20,false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day20_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 100; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(1016700771200474L, Day20.task2(input, 100, 20,false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
