package tasks;

import org.example.Day13;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day13Test
{
  private static final StringInput TEST_INPUT = new StringInput(
    """
Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279
    """.trim());

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(480L, Day13.task1(TEST_INPUT, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day13_task.txt");
    Assertions.assertEquals(26005L, Day13.task1(input, false));
  }

  @Test
  public void task2Test1()
  {
    Assertions.assertEquals(875318608908L, Day13.task2(TEST_INPUT, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day13_task.txt");
    Assertions.assertEquals(105620095782547L, Day13.task2(input, false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day13_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 100000; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(105620095782547L, Day13.task2(input, false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
