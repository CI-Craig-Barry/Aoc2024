package tasks.utils;

import org.example.utils.StringUtils;
import org.junit.jupiter.api.*;

public class StringUtilsTest
{
  @Test
  public void testFindVariables1()
  {
    Object[] objs = StringUtils.findVariables("%d,%d", "232,1");
    Assertions.assertEquals(2, objs.length);
    Assertions.assertEquals(232, objs[0]);
    Assertions.assertEquals(1, objs[1]);
  }

  @Test
  public void testFindVariables2()
  {
    Object[] objs = StringUtils.findVariables("%d|%d", "-2|10");
    Assertions.assertEquals(2, objs.length);
    Assertions.assertEquals(-2, objs[0]);
    Assertions.assertEquals(10, objs[1]);
  }

  @Test
  public void testFindVariables3()
  {
    Object[] objs = StringUtils.findVariables("%d|%s|%d", "-2|something|10");
    Assertions.assertEquals(3, objs.length);
    Assertions.assertEquals(-2, objs[0]);
    Assertions.assertEquals("something", objs[1]);
    Assertions.assertEquals(10, objs[2]);
  }

  @Test
  public void testFindVariables4()
  {
    Assertions.assertThrows(RuntimeException.class,
      () -> {
        Object[] objs = StringUtils.findVariables("%d|%s|%d", "-2|something");
      });
  }
}
