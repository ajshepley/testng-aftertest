package com.github.ajshepley;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.testng.IClass;
import org.testng.ITestResult;
import org.testng.annotations.Test;

public class SafeAfterTestMethodListenerTest {

  @Test
  public void testNoTestClass() {
    final SafeAfterTestMethodListener safeAfterTestMethodListener =
        new SafeAfterTestMethodListener();

    final TestClass testClass = new TestClass();

    final int initialExecutionCount = testClass.getExecutionCount();

    assertEquals(initialExecutionCount, 0);

    final FakeTestResult testResult = new FakeTestResult(testClass);

    final Throwable resultException = CollectionTools.expectException(
        IllegalStateException.class,
        () -> safeAfterTestMethodListener.onTestSuccess(testResult)
    );

    assertNotNull(resultException);
    assertEquals(resultException.getMessage(), "Test result does not have a test class.");

    final int resultExecutionCount = testClass.getExecutionCount();
    assertEquals(resultExecutionCount, 0);
  }

  @Test
  public void testAfterTestMethod() {
    final SafeAfterTestMethodListener safeAfterTestMethodListener =
        new SafeAfterTestMethodListener();

    final TestClass testClass = new TestClass();

    final int initialExecutionCount = testClass.getExecutionCount();

    assertEquals(initialExecutionCount, 0);

    final FakeTestResult testResult = new FakeTestResult(testClass);
    final IClass testIClass = new TestClassImpl(TestClass.class);
    testResult.setTestClass(testIClass);

    safeAfterTestMethodListener.onTestSuccess(testResult);

    final int resultExecutionCount = testClass.getExecutionCount();

    assertEquals(resultExecutionCount, 1);
  }

  @Test
  public void testMethodsExecuteInOrder() {
    final SafeAfterTestMethodListener safeAfterTestMethodListener =
        new SafeAfterTestMethodListener();

    final TestClassWithOrdering testClassWithOrdering = new TestClassWithOrdering();

    final List<Integer> orderedResults = testClassWithOrdering.getOrderedResults();

    assertEquals(orderedResults.size(), 0);

    final FakeTestResult testResult = new FakeTestResult(testClassWithOrdering);
    final IClass testIClass = new TestClassImpl(TestClassWithOrdering.class);
    testResult.setTestClass(testIClass);

    safeAfterTestMethodListener.onTestSuccess(testResult);

    assertEquals(orderedResults.size(), 3);
    assertEquals((int) orderedResults.get(0), 2);
    assertEquals((int) orderedResults.get(1), 3);
    assertEquals((int) orderedResults.get(2), 1);
  }

  @Test
  public void testErrorInAfterMethod() {
    final SafeAfterTestMethodListener safeAfterTestMethodListener =
        new SafeAfterTestMethodListener();

    final TestClassThrowsError testClassThrowsError = new TestClassThrowsError();

    final int initialCount = testClassThrowsError.getExecutionCount();

    assertEquals(initialCount, 0);

    final FakeTestResult testResult = new FakeTestResult(testClassThrowsError);
    final IClass testIClass = new TestClassImpl(TestClassThrowsError.class);
    testResult.setTestClass(testIClass);

    safeAfterTestMethodListener.onTestFailedButWithinSuccessPercentage(testResult);

    final int endingCount = testClassThrowsError.getExecutionCount();
    final Exception expectedException = testClassThrowsError.generateException();

    final Throwable resultException = testResult.getThrowable();
    assertEquals(resultException.getClass(), InvocationTargetException.class);

    final InvocationTargetException invocationTargetException =
        (InvocationTargetException) resultException;

    final Throwable causeException = invocationTargetException.getTargetException();

    assertEquals(endingCount, 1);
    assertEquals(testResult.getStatus(), ITestResult.FAILURE);
    assertEquals(causeException.getClass(), expectedException.getClass());
    assertEquals(causeException.getMessage(), expectedException.getMessage());
  }

  @Test
  public void testPrivateMethodsNotExecuted() {
    final SafeAfterTestMethodListener safeAfterTestMethodListener =
        new SafeAfterTestMethodListener();

    final TestClassWithInaccessibleAfterMethod testClassWithInaccessibleAfterMethod =
        new TestClassWithInaccessibleAfterMethod();

    final int initialCount = testClassWithInaccessibleAfterMethod.getExecutionCount();

    assertEquals(initialCount, 0);

    final FakeTestResult testResult = new FakeTestResult(testClassWithInaccessibleAfterMethod);
    final IClass testIClass = new TestClassImpl(TestClassWithInaccessibleAfterMethod.class);
    testResult.setTestClass(testIClass);

    // Should return early - the methods should not be found.
    safeAfterTestMethodListener.onTestFailure(testResult);

    final int endingCount = testClassWithInaccessibleAfterMethod.getExecutionCount();

    assertEquals(endingCount, 0);
    assertEquals(testResult.getStatus(), ITestResult.CREATED);
  }

  @SuppressWarnings("unused")
  private static class TestClass {

    private int executionCount = 0;

    @SafeAfterTestMethod
    public void myAfterMethod() {
      ++this.executionCount;
    }

    public int getExecutionCount() {
      return this.executionCount;
    }
  }

  @SuppressWarnings("unused")
  private static class TestClassWithOrdering {

    // Alternatively, Mockito.inOrder could be used, but would require the extra dependency.
    private final List<Integer> orderedResults = new ArrayList<>();

    @SafeAfterTestMethod(priority = 3)
    public void myAfterMethod1() {
      System.out.println("TestClassWithOrdering.AfterMethod 1 called.");
      this.orderedResults.add(1);
    }

    @SafeAfterTestMethod(priority = 1)
    public void myAfterMethod2() {
      System.out.println("TestClassWithOrdering.AfterMethod 2 called.");
      this.orderedResults.add(2);
    }

    @SafeAfterTestMethod(priority = 2)
    public void myAfterMethod3() {
      System.out.println("TestClassWithOrdering.AfterMethod 3 called.");
      this.orderedResults.add(3);
    }

    List<Integer> getOrderedResults() {
      return this.orderedResults;
    }
  }

  @SuppressWarnings("unused")
  private static class TestClassWithInaccessibleAfterMethod {

    private int executionCount = 0;

    @SafeAfterTestMethod
    private void myAfterMethod() {
      // Should not be accessed or called.
      ++executionCount;
      fail("Inaccessible method executed.");
    }

    int getExecutionCount() {
      return this.executionCount;
    }
  }

  @SuppressWarnings("unused")
  private static class TestClassThrowsError {

    private int executionCount = 0;

    @SafeAfterTestMethod
    public void myAfterMethod() {
      // Should not be accessed or called.
      ++executionCount;
      throw this.generateException();
    }

    int getExecutionCount() {
      return this.executionCount;
    }

    RuntimeException generateException() {
      return new RuntimeException("Some test error.");
    }
  }
}
