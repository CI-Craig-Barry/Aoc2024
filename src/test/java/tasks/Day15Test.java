package tasks;

import org.example.Day15;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import tasks.utils.PerformanceTester;
import utils.FileUtils;

public class Day15Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput(
    """
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<
    """.trim());

  private static final StringInput TEST_INPUT_2 = new StringInput(
    """
#######
#...#.#
#.....#
#..OO@#
#..O..#
#.....#
#######

<vv<<^^<<^^
    """.trim());

  private static final StringInput TEST_INPUT_3 = new StringInput(
    """
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
    """.trim());

  @Test
  public void task1Test1()
  {
    Assertions.assertEquals(2028, Day15.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1Test2()
  {
    Assertions.assertEquals(10092, Day15.task1(TEST_INPUT_3, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day15_task.txt");
    Assertions.assertEquals(1463512L, Day15.task1(input,false));
  }

  @Test
  public void task2Test1()
  {
    Assertions.assertEquals(616L, Day15.task2(TEST_INPUT_2, true));
  }

  @Test
  public void task2Test2()
  {
    Assertions.assertEquals(9021L, Day15.task2(TEST_INPUT_3, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day15_task.txt");

    //Attempt 1 - 751852 (too low)
    //Attempt 2 - 1480004 (too low)
    Assertions.assertEquals(1486520L, Day15.task2(input, false));
  }

  @Test
  public void task2Performance()
  {
    StringInput input = FileUtils.getFileContents("/day15_task.txt");
    PerformanceTester tester = new PerformanceTester();
    for(int i = 0; i < 1000; i++)
    {
      tester.startTiming();
      Assertions.assertEquals(1486520L, Day15.task2(input,false));
      tester.stopTiming();
    }

    tester.printResults();
  }
}
