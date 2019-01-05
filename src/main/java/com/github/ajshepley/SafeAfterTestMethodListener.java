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

public class SafeAfterTestMethodListener implements ITestListener {

  private static final Class<SafeAfterTestMethod> AFTER_TEST_ANNOTATION_CLASS = SafeAfterTestMethod.class;

  private static final Comparator<Method> AFTER_TEST_METHOD_COMPARATOR = Comparator.comparing(
      comparedMethod -> comparedMethod.getAnnotation(AFTER_TEST_ANNOTATION_CLASS).priority()
  );

  private final Map<Class, List<Method>> afterTestMethods = new ConcurrentHashMap<>();

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

  @Override
  public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
    // No-op
  }

  @Override
  public void onStart(final ITestContext context) {
    // onStart does not reliably execute for each test class, depending on the calling test context.
    // Therefore we do the work after test method completion, we cache the after methods found
    // per class, and we no-op here.
    // Due to potential parallelism of test suites, this can't simply be the list of methods.
  }

  @Override
  public void onFinish(final ITestContext context) {
    // no-op
    System.out.println(this.afterTestMethods.toString());
    this.afterTestMethods.values().forEach(value -> System.out.println(value.toString()));
  }
}
