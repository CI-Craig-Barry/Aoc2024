package tasks;

import org.example.*;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import utils.FileUtils;

public class Day22Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
1
10
100
2024
    """.trim());

  private static final StringInput TEST_INPUT_2 = new StringInput(
    """
1
2
3
2024
    """.trim());

  @Test
  public void part1Test1()
  {
    Assertions.assertEquals(37327623L, Day22.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day22_task.txt");

    Assertions.assertEquals(16039090236L, Day22.task1(input, true));
  }

  @Test
  public void randomTest1()
  {
    Day22.Secret secret = new Day22.Secret(123);
    Day22.Sequence sequence = Day22.Sequence.make(-1, -1, 0, 2);
    Assertions.assertEquals(6, Day22.numBananasWithSequence(secret, sequence));
  }

  @Test
  public void randomTest2()
  {
    var secrets = Day22.makeSecrets(TEST_INPUT_2);
    Day22.Sequence sequence = Day22.Sequence.make(-2, 1, -1, 3);
//    Day22.Sequence sequence = Day22.Sequence.make(3, -1, 1, 2);
    Assertions.assertEquals(23, Day22.numBananasTotal(secrets, sequence));
  }

  @Test
  public void part2Test1()
  {
    Assertions.assertEquals(23L, Day22.task2(TEST_INPUT_2, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day22_task.txt");
    Assertions.assertEquals(1808L, Day22.task2(input, true));
  }
}
