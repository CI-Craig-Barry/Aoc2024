package org.example;

import org.example.utils.StringInput;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Day22
{
  private static boolean debug;

  public static long task1(StringInput input, boolean debug)
  {
    Day22.debug = debug;

    List<Secret> secrets = makeSecrets(input);

    long total = 0;
    for (Secret secret : secrets)
    {
      for(int i = 0; i < 2000; i++)
      {
        secret.next();
      }
      total += secret.getSecret();
    }

    return total;
  }

  public static long task2(StringInput input, boolean debug)
  {
    List<Secret> secrets = makeSecrets(input);

    return secrets
      .parallelStream()
      .map(secret -> {
        Set<Sequence> visited = new HashSet<>();
        Map<Sequence, Integer> results = new HashMap<>();

        for(int i = 0; i < N_ITERATIONS; i++)
        {
          secret.next();
          var sequence = secret.getSequence().copy();

          //If this is a valid sequence
          if(sequence.isFull())
          {
            //If we've already visited it, ignore it
            if(visited.contains(sequence))
            {
              continue;
            }

            visited.add(sequence);
            results.put(sequence, secret.getPrice());
          }
        }

        return results;
      })
      .reduce((map1, map2) -> {
        Map<Sequence, Integer> combinedMap = new HashMap<>(map2);
        for (Map.Entry<Sequence, Integer> entry : map1.entrySet())
        {
          var key = entry.getKey();
          int result = combinedMap.getOrDefault(key, 0) + entry.getValue();
          combinedMap.put(key, result);
        }
        return combinedMap;
      })
      .orElse(Map.of())
      .values()
      .stream()
      .mapToInt(v->v)
      .max()
      .orElse(0);
  }

  //This is how I originally solved it & I'm so stupidly proud that it worked
  //that I'm keeping it. It is not a good solution but it should've never worked anyway
  public static long task2BruteForce(StringInput input, boolean debug)
  {
    Day22.debug = debug;

    List<Secret> secrets = makeSecrets(input);
    Set<Sequence> sequences = calculateUniqueSequences(secrets);

    Map<Sequence, Long> totalBananasCount = new ConcurrentHashMap<>();
    final AtomicInteger sequenceIdx = new AtomicInteger();

    running = true;

    //Thread to print how long is left
    Thread.ofVirtual().start(() ->
    {
      while(running)
      {
        int sequenceIdxVal = sequenceIdx.get();
        double ratioComplete = (double) sequenceIdxVal / sequences.size();
        double pctComplete = 100d * ratioComplete;

        System.out.println(String.format("Completed: %d / %d, %.2f%%",
          sequenceIdxVal, sequences.size(), pctComplete));
        try
        {
          Thread.sleep(5000L);
        }
        catch (InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }
    });


    //Just brute force it, for fun & profit
    sequences.parallelStream().forEach(sequence -> {
      totalBananasCount.put(sequence, numBananasTotal(secrets, sequence));
      sequenceIdx.getAndIncrement();
    });

    running = false;
    assert sequences.size() == totalBananasCount.size();

    return totalBananasCount.values()
      .stream()
      .mapToInt(Long::intValue)
      .max()
      .orElse(0);
  }

  private static boolean running = false;

  //Generate secret instances from string input
  public static List<Secret> makeSecrets(StringInput input)
  {
    List<Secret> secrets = new ArrayList<>();
    for (String line : input.asLines())
    {
      secrets.add(new Secret(Long.parseLong(line.trim())));
    }

    return secrets;
  }

  //Calculate the total number of bananas you'd get with a sequence across all secrets
  public static long numBananasTotal(List<Secret> secrets, Sequence expectedSequence)
  {
    long total = 0;

    for (Secret secret : secrets)
    {
      total += numBananasWithSequence(secret, expectedSequence);
    }

    return total;
  }

  //Calculate number of bananas you get from a secret with a given sequence
  public static int numBananasWithSequence(Secret secret, Sequence expectedSequence)
  {
    //We use copies here for multi-threading
    Secret copy = secret.resetCopy();

    for(int i = 0; i < N_ITERATIONS; i++)
    {
      copy.next();

      if(copy.matchesSequence(expectedSequence))
      {
        return copy.getPrice();
      }
    }

    return 0;
  }

  //Calculate all possible sequences that could appear in every sequence
  public static Set<Sequence> calculateUniqueSequences(List<Secret> allSecrets)
  {
    HashSet<Sequence> sequences = new HashSet<>();

    int secretIdx = 0;

    for (Secret secret : allSecrets)
    {
      Secret copy = secret.resetCopy();

      for(int i = 0; i < N_ITERATIONS; i++)
      {
        copy.next();

        if(copy.getSequence().isFull())
        {
          sequences.add(copy.getSequence().copy());
        }
      }

      System.out.println("Completed secret " + ++secretIdx);
    }

    return sequences;
  }

  //Stores a secret & its current state of being iterated
  public static class Secret
  {
    public Secret(long starter)
    {
      this.baseSecret = starter;
      this.secret = starter;
    }

    //Generate the next secret
    public void next()
    {
      int lastPrice = getPrice();
      long newSecret = secret ^ (secret * 64L);
      newSecret %= 16777216L;

      newSecret = newSecret ^ (newSecret / 32L);
      newSecret %= 16777216L;

      newSecret = newSecret ^ (newSecret * 2048);
      newSecret %= 16777216L;

      this.secret = newSecret;

      //Update current sequence
      int newPrice = getPrice();
      int diff = newPrice - lastPrice;
      this.sequence.add(diff);
    }

    //Check if the sequence currently matches the last 4 price changes
    public boolean matchesSequence(Sequence sequence)
    {
      return this.sequence.equals(sequence) && this.sequence.isFull();
    }

    //Create a copy of this secret which is reset to the base secret
    public Secret resetCopy()
    {
      Secret newSecret = new Secret(this.baseSecret);
      return newSecret;
    }

    //Get the current secret
    public long getSecret()
    {
      return this.secret;
    }

    //Get the current print
    public int getPrice()
    {
      return (int) (secret % 10);
    }

    //Get the current last sequence of 4 price changes
    public Sequence getSequence()
    {
      return sequence;
    }

    private long secret;
    private final long baseSecret;
    private final Sequence sequence = new Sequence();
  }

  //Stores the difference between prices for the last 4 prices
  public static class Sequence
  {
    public static Sequence make(int a, int b, int c, int d)
    {
      Sequence sequence = new Sequence();
      sequence.add(a);
      sequence.add(b);
      sequence.add(c);
      sequence.add(d);
      return sequence;
    }

    //Copy the sequence
    public Sequence copy()
    {
      Sequence newSeq = new Sequence();
      newSeq.sequence = this.sequence;
      newSeq.iterations = this.iterations;
      return newSeq;
    }

    //Add a number to the sequence
    public void add(int next)
    {
      sequence <<= 7;
      sequence += Math.abs(next) & 0x3F; // Only take the bottom 6 bytes

      //If next is negative, set the 7th bit
      if(next < 0)
      {
        sequence |= (1 << 6);
      }

      //Only keep 28 bits
      sequence &= 0xFFFFFFF;
      iterations++;
    }

    //Check there are atleast 4 numbers in the sequence
    public boolean isFull()
    {
      return iterations >= 4;
    }

    //Retrieve the numbers of the sequence
    public List<Integer> getNumbers()
    {
      return List.of(
        getNum(0),
        getNum(1),
        getNum(2),
        getNum(3)
      );
    }

    //Get a number in the sequence (idx should be 0 to 3)
    private int getNum(int idx)
    {
      //Get the absolute value
      int value = (sequence & (0x7F << (idx * 7))) >> (idx * 7);

      //If value is negative (7th bit set)
      if((value & 0x40) == 0x40)
      {
        return -(value & ~(0x40));
      }

      return value & 0x3F;
    }

    @Override
    public boolean equals(Object o)
    {
      if (!(o instanceof Sequence sequence1))
      {
        return false;
      }
      return sequence == sequence1.sequence;
    }

    @Override
    public int hashCode()
    {
      return Objects.hash(sequence);
    }

    @Override
    public String toString()
    {
      return Arrays.toString(getNumbers().toArray(Integer[]::new));
    }

    private int sequence;
    private int iterations = 0;
  }

  private static final int N_ITERATIONS = 2000;
}
