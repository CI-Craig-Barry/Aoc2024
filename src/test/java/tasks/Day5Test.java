//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.Day5;
import org.example.utils.*;
import org.junit.jupiter.api.*;
import utils.FileUtils;

import java.util.*;

public class Day5Test
{
  @Test
  public void testTask1()
  {
    String input =
      """
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
""";

    List<String> lines = StringUtils.splitIntoLines(input);
    Map<Integer, TopologicalSortable> dependencies = Day5.mapDependencies(lines);
    List<Day5.ManualOrder> manualOrders = Day5.makeOrderings(lines);

    Assertions.assertEquals(manualOrders.size(), 6);
    Assertions.assertTrue(manualOrders.get(0).isOrderedCorrectly(dependencies));
    Assertions.assertTrue(manualOrders.get(1).isOrderedCorrectly(dependencies));
    Assertions.assertTrue(manualOrders.get(2).isOrderedCorrectly(dependencies));
    Assertions.assertFalse(manualOrders.get(3).isOrderedCorrectly(dependencies));
    Assertions.assertFalse(manualOrders.get(4).isOrderedCorrectly(dependencies));
    Assertions.assertFalse(manualOrders.get(5).isOrderedCorrectly(dependencies));

//    Assertions.assertEquals(18, Day5.task1(StringUtils.splitIntoLines(input)));
  }

  @Test
  public void testTask1_2()
  {
    String input =
      """
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
""";

    Assertions.assertEquals(143, Day5.task1(StringUtils.splitIntoLines(input)));
  }

  @Test
  public void task1()
  {
    String input = FileUtils.getFileAsString("/day5_task.txt").trim();

    int result = Day5.task1(StringUtils.splitIntoLines(input));
    Assertions.assertEquals(5108, result);
  }

  @Test
  public void testTask2()
  {
    String input =
      """
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
""";
    Assertions.assertEquals(123, Day5.task2(StringUtils.splitIntoLines(input)));
  }

  @Test
  public void task2()
  {
    String input = FileUtils.getFileAsString("/day5_task.txt").trim();

    int result = Day5.task2(StringUtils.splitIntoLines(input));
    Assertions.assertEquals(7380, result);
  }
}
