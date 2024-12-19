//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.Day19;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day19Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb    
    """.trim());

  @Test
  public void part1Test1()
  {
    Assertions.assertEquals(6L, Day19.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day19_task.txt");
    //122 - too low
    Assertions.assertEquals(213L, Day19.task1(input, false));
  }

  @Test
  public void part2Test1()
  {
    Assertions.assertEquals(16l, Day19.task2(TEST_INPUT_1, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day19_task.txt");
    Assertions.assertEquals(1016700771200474L, Day19.task2(input, false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day19_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 100; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(1016700771200474L, Day19.task2(input, false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
