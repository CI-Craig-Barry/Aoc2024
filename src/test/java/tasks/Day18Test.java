//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.Day18;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day18Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0
    """.trim());

  @Test
  public void part1Test1()
  {
//    OO,#,,,
//    ,O#OOO,
//    ,OOO#O,
//    ,,,#OO#
//    ,,#OO#,
//    ,#,O#,,
//    #,#OOOO

//    OO.#OOO
//    .O#OO#O
//    .OOO#OO
//    ...#OO#
//    ..#OO#.
//    .#.O#..
//    #.#OOOO


    Assertions.assertEquals(22L, Day18.task1(TEST_INPUT_1, 7, 12, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day18_task.txt");
//    System.out.print("nLines: " + input.asLines().size());

    Assertions.assertEquals(0L, Day18.task1(input, 71, 1024, true));
  }

  @Test
  public void part2Test1()
  {
    Assertions.assertEquals("6,1", Day18.task2(TEST_INPUT_1, 7, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day18_task.txt");
    Assertions.assertEquals("15,20", Day18.task2(input, 71, false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day18_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 10_000; i++)
    {
      tester.startTiming();
      Assertions.assertEquals("15,20", Day18.task2(input, 71, false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
