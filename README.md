# testng-aftertest

An annotation and listener designed to "safely" execute methods and calls after a TestNG unit test has completed.

Methods marked with this annotation will be executed after a test case has completed, whether it passed or failed.

```java
@SafeAfterTestMethod(priority = 1)
public void myTearDownMethod() {
  this.someSharedTestObject = null;
  this.someTeardownMethodThatCanThrowAnException();
  Mockito.verifyNoMoreInteractions(this.myMock1, this.myMock2, this.myMock3);
}
```

If at least one test class in the project is marked with the Listener class, then the methods will be considered and processed.

```java
@Listeners(SafeAfterTestMethodListener.class)
public class MyTestClass() {
  ...
}
```

If an exception occurs inside a method annotated with `@SafeAfterTestMethod`, then that exception will be appended to the TestNG result.

The original failure exception will also be preserved.

____
## Rationale

TestNG has some different ideological behaviours that differ from JUnit and other testing frameworks.

One of these is that AfterTest, AfterClass, AfterMethod and similarly annotated methods pertain to the Test class context rather than the individual tests.

They are meant to serve as test class-level cleanup or setup mechanisms, rather than per-test machinations.

This means that any _failures_ in those methods can result in the entire test class failing, and each unit test being marked as  a failure or not executing.

For example:

```java
// Not an example of good test code. Merely illustrative.
public class MyTestClass {
  String sharedState = null;
  
  @AfterMethod(alwaysRun = true) // TestNG afterMethod
  public void cleanup() {
    assertThat(this.sharedState, is(null)); // Example of call that will error if tests fail.
  }
  
  @Test
  public void myTest1() {
    this.sharedState = something;
    /// ... test code ...
    this.sharedState = null;
  }
  
  @Test
  public void myTest2() {
      this.sharedState = something;
      /// ... test code ...
      this.sharedState = null;
  }
  
  @Test
  public void myTest3() {
      this.sharedState = something;
      /// ... test code ...
      this.sharedState = null;
  }
}
```

If any of the test code throws an exception or ends the test before the cleanup code is executed, the AfterMethod will fail. That, in turn, will cause the rest of the tests to be aborted.

This is by design - TestNG does not want AfterX methods to be considered part of a test, and so a failure represents an issue with the test as a whole. The whole test is suspect.

However, in practice you may have test classes with a ton of unit tests and a single failure like this can render them difficult to troubleshoot. Additionally, you may have code that you want to share between tests automatically, that can indicate a failure with a specific test, but not compromise the entire test suite upon failure.

This annotation and listener provides a way to have cleanup or verification tasks whose failure is constrained to only the executing test method.
____

## Building and Dependencies

The included Gradle4 wrappers can be used to build the project (`./gradlew` or `.\gradlew.bat`).

This repo is meant more as an illustration of how to work around this TestNG AfterX behaviour than to serve directly as a dependency.

However, a classes dependency jar can be created using the `jar` task. This is included as a default build task.

The TestNG version can be tweaked in the `ext` block in `build.gradle`.

____

## Project Setup

A `.idea/` directory is included, which has a variation of [Google's java style](https://google.github.io/styleguide/javaguide.html) called "GoogleStyle-Chop".

You can use that directory to instantiate the project with IntelliJ IDEA, or you can import the Gradle project in your IDE.

A unit test class is provided for the main listener, and there are example unit tests that _utilize_ the listener with logging.
test
