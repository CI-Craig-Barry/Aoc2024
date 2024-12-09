//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.*;
import org.example.utils.*;
import org.junit.jupiter.api.*;
import utils.FileUtils;

import java.util.List;

public class Day9Test
{
  private static final StringInput TEST_INPUT = new StringInput(
"""
2333133121414131402
""".trim());

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(1928L, Day9.task1(TEST_INPUT, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day9_task.txt");
    Assertions.assertEquals(6242766523059L, Day9.task1(input, false));
  }

  @Test
  public void task2Test()
  {
    Assertions.assertEquals(2858L, Day9.task2(TEST_INPUT, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day9_task.txt");

    //Attempt 1: 8444425634594
    //Attempt 2: 8448678072073
    //Attempt 3: 5223834469207
    Assertions.assertEquals(6272188244509L, Day9.task2(input, false));
  }
}
