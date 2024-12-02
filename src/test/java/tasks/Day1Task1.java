package tasks;

import org.example.Day1Task1Parser;
import org.junit.jupiter.api.*;
import utils.FileUtils;

import java.util.List;

public class Day1Task1
{
  @Test
  public void run()
  {
    List<String> lines = FileUtils.getLines("/day1_task1.txt");
    int result =  Day1Task1Parser.parseLines(lines);
    Assertions.assertEquals(1660292, result);
  }
}
