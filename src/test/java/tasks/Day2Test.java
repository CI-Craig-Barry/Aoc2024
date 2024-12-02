package tasks;

import org.example.Day2;
import org.junit.jupiter.api.*;
import utils.FileUtils;

import java.util.*;

public class Day2Test
{
  @Test
  public void task1Test()
  {
    String testInput = """
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
      """;

    List<String> testInputLines = Arrays.stream(testInput.split("\n")).map(String::trim).toList();
    Assertions.assertEquals(2, Day2.task1(testInputLines));
  }

  @Test
  public void task1()
  {
    List<String> lines = FileUtils.getLines("/day2_task1.txt");
    Assertions.assertEquals(502, Day2.task1(lines));
  }

  @Test
  public void task2Test()
  {
    String testInput = """
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
      """;

    List<String> testInputLines = Arrays.stream(testInput.split("\n")).map(String::trim).toList();
    Assertions.assertEquals(4, Day2.task2(testInputLines));
  }

  @Test
  public void task2()
  {
    List<String> lines = FileUtils.getLines("/day2_task1.txt");
    Assertions.assertEquals(544, Day2.task2(lines));
  }
}
