package tasks;

import org.example.*;
import org.junit.jupiter.api.*;
import utils.FileUtils;

import java.util.*;

public class Day1Task2
{
  @Test
  public void run()
  {
    List<String> lines = FileUtils.getLines("/day1_task1.txt");
    int result = Day1Task2Parser.run(lines);
    Assertions.assertEquals(22776016, result);
  }
}
