package com.github.ajshepley;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.ws.Action;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;

public class SafeAfterTestListener implements ITestListener {

  private final Map<Class, List<Method>> afterTestMethods = new HashMap<>();

  @Override
  public void onTestStart(final ITestResult result) {
    this.processAfterMethods(result);
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
    System.out.println(this.afterTestMethods.toString());
    this.afterTestMethods.values().forEach(value -> System.out.println(value.toString()));
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(final ITestResult result) {
    // No-op
  }

  @Override
  public void onStart(final ITestContext context) {
    final XmlTest currentXmlTest = context.getCurrentXmlTest();

    final List<XmlClass> xmlClasses = currentXmlTest.getXmlClasses();

    if (xmlClasses.isEmpty()) {
      throw new IllegalStateException("No test classes were found.");
    }

    final XmlClass xmlClass = xmlClasses.get(0);
    final Class<?> supportClass = xmlClass.getSupportClass();

    final Method[] methods = supportClass.getMethods();

    final List<Method> afterMethodsForTestClass = Stream.of(methods)
        .filter(this::containsMatchingAnnotation)
        .sorted(compareAnnotatedMethods())
        .collect(Collectors.toList());

    this.afterTestMethods.put(supportClass, afterMethodsForTestClass);
  }

  private boolean containsMatchingAnnotation(final Method method) {
    return method.getAnnotation(Action.class) != null;
  }

  private Comparator<Method> compareAnnotatedMethods() {
    return Comparator.comparing(comparedMethod -> comparedMethod.getAnnotation(Action.class).hashCode());
  }

  @Override
  public void onFinish(final ITestContext context) {
    // no-op
  }
}
