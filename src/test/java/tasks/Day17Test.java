//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.*;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Day17Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
    """.trim());

  @Test
  public void testTask1()
  {
    Assertions.assertEquals("4,6,3,5,6,3,5,2,1,0", Day17.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day17_task.txt");

    //Attempt 1 - 7,7,7,7,7,7,7,7,3
    Assertions.assertEquals("7,1,5,2,4,0,7,6,1", Day17.task1(input, false));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day17_task.txt");

    long value = Day17.task2(input, false);
    System.out.println("Value: " + value);

    Day17.Program program = Day17.parseProgram(input);
    Day17.debug = true;
    program.registerA = value;
    String output = program.run();

    System.out.println("Output: " + output + "[Size:" + output.split(",").length + "]");

    StringJoiner joiner = new StringJoiner(",");
    program.instructions.forEach(inst -> joiner.add(String.valueOf(inst)));
    Assertions.assertEquals(joiner.toString(), output);

    //Result = 37222273957364
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day17_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 10_000; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(37222273957364L, Day17.task2(input, false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
