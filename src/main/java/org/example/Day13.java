package org.example;

import org.example.utils.*;
import org.example.utils.Vector;

import java.util.*;

public class Day13
{
  private static boolean debug = false;

  public static long task1(StringInput input, boolean debug)
  {
    Day13.debug = debug;
    List<ClawMachine> clawMachines = parseClawMachines(input, 0L);

    final int MAX_PRESSES = 100;
    long tokensSpent = 0;

    for (ClawMachine clawMachine : clawMachines)
    {
      PressButtonResult bestResult = null;

      for(int a = 0; a < MAX_PRESSES; a++)
      {
        for(int b = 0; b < MAX_PRESSES; b++)
        {
          var result = clawMachine.press(a, b);
          if(result.obtainedPrize)
          {
            bestResult = bestResult == null ?
              result :
              (result.tokensSpent < bestResult.tokensSpent ? result : bestResult);
          }
        }
      }

      tokensSpent += bestResult != null ? bestResult.tokensSpent : 0;

      if(debug)
      {
        System.out.println("machine: " + clawMachine.toString());
        System.out.println("result: " + (bestResult == null ? "none" : bestResult.toString()));
      }
    }

    return tokensSpent;
  }

  public static long task2(StringInput input, boolean debug)
  {
    Day13.debug = debug;
    List<ClawMachine> clawMachines = parseClawMachines(input, 10000000000000L);

    long tokensSpent = 0;

    for (ClawMachine clawMachine : clawMachines)
    {
      final long aX = clawMachine.buttonA.getX();
      final long aY = clawMachine.buttonA.getY();
      final long bX = clawMachine.buttonB.getX();
      final long bY = clawMachine.buttonB.getY();
      final long pX = clawMachine.prizePosition.getX();
      final long pY = clawMachine.prizePosition.getY();

      final long aPressed = ((pX * bY) - (bX * pY)) / ((aX * bY) - (bX * aY));
      final long bPressed = (pY - (aPressed * aY)) / bY;

      if((aX * aPressed) + (bX * bPressed) == pX &&
        (aY * aPressed) + (bY * bPressed) == pY)
      {
        tokensSpent += (aPressed * 3) + bPressed;
      }
    }

    return tokensSpent;
  }

  private static List<ClawMachine> parseClawMachines(StringInput input, long prizeOffset)
  {
    List<ClawMachine> machines = new ArrayList<>();
    Iterator<String> iter = input.asLines().iterator();

    //I had fun here to make it speedy
    while(iter.hasNext())
    {
      String str = iter.next();
      int ax = Integer.parseInt(str.substring(12, 14));
      int ay = Integer.parseInt(str.substring(18));
      str = iter.next();
      int bx = Integer.parseInt(str.substring(12, 14));
      int by = Integer.parseInt(str.substring(18));
      str = iter.next();
      int comaIdx = str.indexOf(",", 12);
      int yEqualsIdx = comaIdx + 3;
      int px = Integer.parseInt(str.substring(9, comaIdx));
      int py = Integer.parseInt(str.substring(yEqualsIdx + 1));

      ClawMachine machine = new ClawMachine();
      machine.buttonA = Vector.makeXYvector(ax, ay);
      machine.buttonB = Vector.makeXYvector(bx, by);
      machine.prizePosition = Point.makeXYPoint(px + prizeOffset, py + prizeOffset);
      machines.add(machine);

      if(iter.hasNext())
      {
        iter.next();
      }
    }

    return machines;
  }

  private static class ClawMachine
  {
    public PressButtonResult press(int a, int b)
    {
      PressButtonResult result = new PressButtonResult();

      result.finalPoint = Point.makeXYPoint(0, 0);
      result.finalPoint = result.finalPoint.add(Vector.scale(buttonA, a));
      result.finalPoint = result.finalPoint.add(Vector.scale(buttonB, b));

      result.tokensSpent = a * 3 + b;
      result.buttonsPressed = a + b;
      result.aPressed = a;
      result.bPressed = b;
      result.obtainedPrize = result.finalPoint.equals(prizePosition);

      return result;
    }

    @Override
    public String toString()
    {
      return "ClawMachine{" +
        "buttonA=" + buttonA +
        ", buttonB=" + buttonB +
        ", prizePosition=" + prizePosition +
        ", currentPosition=" + currentPosition +
        '}';
    }

    private Vector buttonA;
    private Vector buttonB;
    private Point prizePosition;
    private Point currentPosition;
  }

  private static class PressButtonResult
  {
    @Override
    public String toString()
    {
      return "PressButtonResult{" +
        "finalPoint=" + finalPoint +
        ", obtainedPrize=" + obtainedPrize +
        ", tokensSpent=" + tokensSpent +
        ", buttonsPressed=" + buttonsPressed +
        ", aPressed=" + aPressed +
        ", bPressed=" + bPressed +
        '}';
    }

    private Point finalPoint;
    private boolean obtainedPrize;
    private int tokensSpent;
    private int buttonsPressed;
    private int aPressed;
    private int bPressed;
  }
}
