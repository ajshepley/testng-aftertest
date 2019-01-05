package com.github.ajshepley;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.testng.IClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * SafeAfterTestMethodListener
 *
 * This class will find and execute methods in a test class annotated with @SafeAfterTestMethod.
 *
 * These methods will be executed in order specified by their Priority values (default 0).
 * Methods are cached between invocations of tests in a given class.
 *
 * This execution occurs for any test class in an execution context, so long as any single Test class
 * instance is annotated with @Listener(SafeAfterTestMethodListener.class).
 *
 * See com.github.ajshepley.ExampleTest for example of use.
 * @see SafeAfterTestMethod
 */
public class SafeAfterTestMethodListener implements ITestListener {

  private static final Class<SafeAfterTestMethod> AFTER_TEST_ANNOTATION_CLASS =
      SafeAfterTestMethod.class;

  private static final Comparator<Method> AFTER_TEST_METHOD_COMPARATOR = Comparator.comparing(
      comparedMethod -> comparedMethod.getAnnotation(AFTER_TEST_ANNOTATION_CLASS).priority()
  );

  private final Map<Class, List<Method>> afterTestMethods = new ConcurrentHashMap<>();

  @Override
  public void onStart(final ITestContext context) {
    // onStart does not reliably execute for each test class, depending on the calling test context.
    // Therefore we do the work after test method completion, we cache the after methods found
    // per class, and we no-op here.
    // Due to potential parallelism of test suites, this can't simply be the list of methods.
  }

  @Override
  public void onFinish(final ITestContext context) {
    this.afterTestMethods.clear();
  }

  @Override
  public void onTestStart(final ITestResult result) {
    // no-op
  }

  @Override
  public void onTestSuccess(final ITestResult result) {
    this.processAfterMethods(result);
  }

  @Override
  public void onTestFailure(final ITestResult result) {
    this.processAfterMethods(result);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
    this.processAfterMethods(result);
  }

  @Override
  public void onTestSkipped(final ITestResult result) {
    this.processAfterMethods(result);
  }

  private void processAfterMethods(final ITestResult result) {
    final IClass testClass = result.getTestClass();

    if (testClass == null) {
      throw new IllegalStateException("Test result does not have a test class.");
    }

    final Class<?> realClass = testClass.getRealClass();

    // Cache the afterMethods. @see this.onStart(ITestContext) for more information.
    final List<Method> afterMethods = this.afterTestMethods.computeIfAbsent(
        realClass,
        this::getAfterMethodsForClass
    );

    if (CollectionTools.isNullOrEmpty(afterMethods)) {
      return;
    }

    this.callAfterMethods(afterMethods, result);
  }

  /**
   * Invoke each method on the original test class, and fail any test that receives an exception
   * from the AfterMethod.
   *
   * We explicitly avoid rethrowing the exception in order to allow subsequent tests to complete.
   */
  private void callAfterMethods(final List<Method> afterMethods, final ITestResult testResult) {
    final Object testClassInstance = testResult.getInstance();

    for (final Method afterMethod : afterMethods) {
      try {
        afterMethod.invoke(testClassInstance);
      } catch (final Exception exc) {
        System.out.println(
            "Failed to access afterMethod due to caught exception: "
            + afterMethod.toString()
            + ". Exception: "
            + exc.toString()
        );

        testResult.setStatus(ITestResult.FAILURE);

        // Avoid overriding an existing failure from the test itself.
        if (testResult.getThrowable() == null) {
          testResult.setThrowable(exc);
        }
      }
    }
  }

  private List<Method> getAfterMethodsForClass(final Class realClass) {
    final Method[] methods = realClass.getMethods();

    return Stream.of(methods)
        .filter(this::containsMatchingAnnotation)
        .sorted(AFTER_TEST_METHOD_COMPARATOR)
        .collect(Collectors.toList());
  }

  private boolean containsMatchingAnnotation(final Method method) {
    return method.getAnnotation(AFTER_TEST_ANNOTATION_CLASS) != null;
  }
}
