//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.Day16;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day16Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############
    """.trim());

  private static final StringInput TEST_INPUT_2 = new StringInput(
    """
#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################
    """.trim());


  @Test
  public void task1Test1()
  {
    Assertions.assertEquals(7036L, Day16.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1Test2()
  {
    Assertions.assertEquals(11048L, Day16.task1(TEST_INPUT_2, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day16_task.txt");
    Assertions.assertEquals(127520L, Day16.task1(input,false));
  }

  @Test
  public void task2Test1()
  {
    Assertions.assertEquals(45L, Day16.task2(TEST_INPUT_1, true));
  }

  @Test
  public void task2Test2()
  {
    Assertions.assertEquals(64L, Day16.task2(TEST_INPUT_2, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day16_task.txt");

    //Attempt 1 - 521 (too low)
    Assertions.assertEquals(565L, Day16.task2(input, true));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day16_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 150; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(565L, Day16.task2(input,false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
