package tasks;

import org.example.Day25;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import utils.FileUtils;

public class Day25Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput("""
#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####
    """.trim());

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(3, Day25.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day25_task.txt");
    Assertions.assertEquals(3090L, Day25.task1(input, false));
  }

  //Was no part 2 for day 25
}
