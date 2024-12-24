//
// Copyright (c) 2024, Conserve It Pty. Ltd.
//

package tasks;

import org.example.Day24;
import org.example.utils.StringInput;
import org.junit.jupiter.api.*;
import utils.FileUtils;

public class Day24Test
{
  private static final StringInput TEST_INPUT_1 = new StringInput("""
x00: 1
x01: 1
x02: 1
y00: 0
y01: 1
y02: 0

x00 AND y00 -> z00
x01 XOR y01 -> z01
x02 OR y02 -> z02
    """.trim());

  private static final StringInput TEST_INPUT_2 = new StringInput("""
x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj
    """);

  @Test
  public void task1Test()
  {
    Assertions.assertEquals(4L, Day24.task1(TEST_INPUT_1, true));
  }

  @Test
  public void task1Test2()
  {
    Assertions.assertEquals(2024L, Day24.task1(TEST_INPUT_2, true));
  }

  @Test
  public void task1()
  {
    StringInput input = FileUtils.getFileContents("/day24_task.txt");
    Assertions.assertEquals(4L, Day24.task1(input, false));
  }

  @Test
  public void task2Test()
  {
    Assertions.assertEquals("co,de,ka,ta", Day24.task2(TEST_INPUT_1, true));
  }

  @Test
  public void task2()
  {
    StringInput input = FileUtils.getFileContents("/day24_task.txt");

    Assertions.assertEquals("er,fh,fi,ir,kk,lo,lp,qi,ti,vb,xf,ys,yu", Day24.task2(input, false));
  }
}
