import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExampleTest {

  @BeforeMethod(alwaysRun = true)
  public void setup() {
    System.out.println("Running test setup.");
  }

  @Test
  public void testFail() {
    assertEquals(1, 2);
  }

  @Test
  public void testSuccess() {
    assertTrue(true);
  }
}
