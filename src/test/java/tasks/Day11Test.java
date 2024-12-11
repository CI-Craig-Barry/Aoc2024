//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.*;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day11Test
{
  private static final StringInput TEST_INPUT = new StringInput(
    """
125 17
    """.trim());

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(22L, Day11.task1(TEST_INPUT, 6, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day11_task.txt");
    Assertions.assertEquals(189547L, Day11.task1(input, 25, false));
  }

  @Test
  public void task2Test1()
  {
    Assertions.assertEquals(22L, Day11.task2(TEST_INPUT, 6, true));
  }

  @Test
  public void task2Test2()
  {
    StringInput input = FileUtils.getFileContents("/day11_task.txt");
    Assertions.assertEquals(189547L, Day11.task2(input, 25, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day11_task.txt");
    Assertions.assertEquals(224577979481346L, Day11.task2(input, 75, false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day11_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 1000; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(224577979481346L, Day11.task2(input, 75,false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
