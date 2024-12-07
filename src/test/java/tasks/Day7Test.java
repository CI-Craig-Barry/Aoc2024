package tasks;

import org.example.*;
import org.example.utils.StringUtils;
import org.junit.jupiter.api.*;
import utils.FileUtils;

import java.util.List;

public class Day7Test
{
  private static final String TEST_INPUT = """
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
    """.trim();

  private static final List<String> TEST_INPUT_LINES = StringUtils.splitIntoLines(TEST_INPUT);

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(3749, Day7.task1(TEST_INPUT_LINES));
  }

  @Test
  public void task1()
  {
    List<String> lines = FileUtils.getLines("/day7_task.txt");
    Assertions.assertEquals(2437272016585L, Day7.task1(lines));
  }

  @Test
  public void task2Test()
  {
    Assertions.assertEquals(11387, Day7.task1(TEST_INPUT_LINES));
  }

  @Test
  public void task2()
  {
    List<String> lines = FileUtils.getLines("/day7_task.txt");
    Assertions.assertEquals(162987117690649L, Day7.task1(lines));
  }
}
