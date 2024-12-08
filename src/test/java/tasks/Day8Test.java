package tasks;

import org.example.Day8;
import org.example.utils.StringUtils;
import org.junit.jupiter.api.*;
import utils.FileUtils;

import java.util.List;

public class Day8Test
{
  private static final String TEST_INPUT = """
............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............
    """.trim();

  private static final List<String> TEST_INPUT_LINES = StringUtils.splitIntoLines(TEST_INPUT);

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(14L, Day8.task1(TEST_INPUT_LINES));
  }

  @Test
  public void task1()
  {
    List<String> lines = FileUtils.getLines("/Day8_task.txt");
    Assertions.assertEquals(214L, Day8.task1(lines));
  }

  @Test
  public void task2Test()
  {
    Assertions.assertEquals(34L, Day8.task2(TEST_INPUT_LINES));
  }

  @Test
  public void task2()
  {
    List<String> lines = FileUtils.getLines("/Day8_task.txt");
    Assertions.assertEquals(809L, Day8.task2(lines));
  }
}
