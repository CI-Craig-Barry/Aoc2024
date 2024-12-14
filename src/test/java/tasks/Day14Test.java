package tasks;

import org.example.Day14;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day14Test
{
  private static final StringInput TEST_INPUT = new StringInput(
    """
p=0,4 v=3,-3
p=6,3 v=-1,-3
p=10,3 v=-1,2
p=2,0 v=2,-1
p=0,0 v=1,3
p=3,0 v=-2,-2
p=7,6 v=-1,-3
p=3,0 v=-1,-2
p=9,3 v=2,3
p=7,3 v=-1,2
p=2,4 v=2,-3
p=9,5 v=-3,-3
    """.trim());

  @Test
  public void task1Test1()
  {
    Assertions.assertEquals(12L, Day14.task1(TEST_INPUT, 11, 7, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day14_task.txt");
    Assertions.assertEquals(218433348L, Day14.task1(input, 101, 103, false));
  }

  @Test
  public void task2Test1()
  {
    Assertions.assertEquals(0L, Day14.task2(TEST_INPUT, 101, 103,true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day14_task.txt");
    Assertions.assertEquals(6512L, Day14.task2(input, 101, 103, true));

    //Takes robots 10403 iterations to get back to starting position, has to be lower than that
    //Eventually found it just checking for a map where all robot counts were 1. Felt kind of cheese but oh well
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day14_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 100; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(0L, Day14.task2(input, 101, 103,false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
