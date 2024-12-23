//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.Day23;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import utils.FileUtils;

public class Day23Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput("""
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn
    """.trim());

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(7, Day23.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day23_task.txt");
    Assertions.assertEquals(1323, Day23.task1(input, false));
  }

  @Test
  public void task2Test()
  {
    Assertions.assertEquals("co,de,ka,ta", Day23.task2(TEST_INPUT_1, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day23_task.txt");

    Assertions.assertEquals("er,fh,fi,ir,kk,lo,lp,qi,ti,vb,xf,ys,yu", Day23.task2(input, false));
  }
}
