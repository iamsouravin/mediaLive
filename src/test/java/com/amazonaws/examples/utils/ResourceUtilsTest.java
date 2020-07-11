package com.amazonaws.examples.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResourceUtilsTest {
  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ResourceUtils instance = ResourceUtils.getInstance();

  @TestFactory
  public Stream<DynamicTest> getEnvDynamicTestsFromStream() {
    logger.info("Generating test data set for getEnv...");
    @SuppressWarnings("unchecked")
    TestData<String>[] testDataSet = new TestData[] {
        new TestData<>(null, null, null, null),
        new TestData<>(null, null, "abc", "abc"),
        new TestData<>("", null, null, null),
        new TestData<>("", null, "abc", "abc"),
        new TestData<>(" ", null, null, null),
        new TestData<>(" ", null, "abc", "abc"),
        new TestData<>("TEST", null, null, null),
        new TestData<>("TEST", null, "abc", "abc"),
        new TestData<>("TEST", "", null, null),
        new TestData<>("TEST", "", "abc", "abc"),
        new TestData<>("TEST", " ", null, " "),
        new TestData<>("TEST", " ", "abc", " "),
        new TestData<>("TEST", "abc", null, "abc"),
        new TestData<>("TEST", "abc", "def", "abc"),
    };
    logger.info("Creating stream of dynamic tests...");
    return Stream.of(testDataSet)
        .map(
            testData -> DynamicTest.dynamicTest("Test getEnv: " + testData,
                new TestExecutable<String>(
                    testData,
                    () -> assertEquals(
                        testData.expected,
                        instance.getEnv(testData.name, testData.def)))
            ));
  }

  @TestFactory
  public Stream<DynamicTest> getEnvAsArrayDynamicTestsFromStream() {
    logger.info("Generating test data set for getEnvAsArray...");
    String[] empty = {};
    String[] expectedAbc = {"abc"};
    String[] expectedAbcDef = {"abc", "def"};
    @SuppressWarnings("unchecked")
    TestData<String[]>[] testDataSet = new TestData[] {
        new TestData<>(null, null, null, empty),
        new TestData<>(null, null, "abc", expectedAbc),
        new TestData<>("", null, null, empty),
        new TestData<>("", null, "abc", expectedAbc),
        new TestData<>(" ", null, null, empty),
        new TestData<>(" ", null, "abc", expectedAbc),
        new TestData<>("TEST", null, null, empty),
        new TestData<>("TEST", null, "abc", expectedAbc),
        new TestData<>("TEST", "", null, empty),
        new TestData<>("TEST", "", "abc", expectedAbc),
        new TestData<>("TEST", " ", null, empty),
        new TestData<>("TEST", " ", "abc", empty),
        new TestData<>("TEST", ",", null, empty),
        new TestData<>("TEST", ",", "abc", empty),
        new TestData<>("TEST", ",,", null, empty),
        new TestData<>("TEST", ",,", "abc", empty),
        new TestData<>("TEST", "abc", null, expectedAbc),
        new TestData<>("TEST", "abc", "def", expectedAbc),
        new TestData<>("TEST", "abc,def", null, expectedAbcDef),
        new TestData<>("TEST", " abc", null, expectedAbc),
        new TestData<>("TEST", " abc, def", null, expectedAbcDef),
        new TestData<>("TEST", "\tabc", null, expectedAbc),
        new TestData<>("TEST", "\tabc,\tdef", null, expectedAbcDef),
        new TestData<>("TEST", "abc ", null, expectedAbc),
        new TestData<>("TEST", "abc ,def ", null, expectedAbcDef),
        new TestData<>("TEST", "abc\t", null, expectedAbc),
        new TestData<>("TEST", "abc\t,def\t", null, expectedAbcDef),
        new TestData<>("TEST", " abc ", null, expectedAbc),
        new TestData<>("TEST", " abc , def ", null, expectedAbcDef),
        new TestData<>("TEST", "\tabc\t", null, expectedAbc),
        new TestData<>("TEST", "\tabc\t,\tdef\t", null, expectedAbcDef),
        new TestData<>("TEST", " abc , ,, def ", null, expectedAbcDef),
        new TestData<>("TEST", " \tabc ,\t ,\t\t , \tdef\t ", null, expectedAbcDef)
    };
    logger.info("Creating stream of dynamic tests...");
    return Stream.of(testDataSet)
        .map(
            testData -> DynamicTest.dynamicTest(
                "Test getEnvAsArray: " + testData,
                new TestExecutable<String[]>(
                    testData,
                    () -> assertArrayEquals(
                        testData.expected,
                        instance.getEnvAsArray(testData.name, testData.def)
                    )
                )
            )
        );
  }

  public static class TestExecutable<T> implements Executable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TestData<T> testData;
    private final Executable assertExecutable;

    public TestExecutable(TestData<T> testData, Executable assertExecutable) {
      this.testData = testData;
      this.assertExecutable = assertExecutable;
    }

    @Override public void execute() throws Throwable {
      logger.debug("Executing test with test data: {}...", testData);
      Map<String, String> env = new HashMap<>();
      try {
        if (Objects.nonNull(testData.name) && !testData.name.isEmpty() && Objects.nonNull(
            testData.value)) {
          env.put(testData.name, testData.value);
          TestEnvironmentUtils.setEnv(env);
        }
        assertExecutable.execute();
      } catch (RuntimeException e) {
        logger.error("Error while executing test with test data " + testData, e);
      } finally {
        TestEnvironmentUtils.removeEnv(env);
        env.clear();
      }
    }
  }

  public static class TestData<T> {
    String name;
    String value;
    String def;
    T expected;

    public TestData(String name, String value, String def, T expected) {
      this.name = name;
      this.value = value;
      this.def = def;
      this.expected = expected;
    }

    @Override public String toString() {
      return "TestData{" +
          "name='" + name + '\'' +
          ", value='" + value + '\'' +
          ", def='" + def + '\'' +
          ", expected=" + asString(expected) +
          '}';
    }

    private String asString(T expected) {
      if (Objects.isNull(expected)) return null;
      Class<?> clazz = expected.getClass();
      if (clazz.isArray()) {
        Object[] items = (Object[]) expected;
        String prefix = "";
        StringBuilder builder = new StringBuilder("[");

        clazz = clazz.getComponentType();
        String enclosing = getEnclosing(clazz);
        for (Object item : items) {
          builder.append(prefix)
              .append(enclosing)
              .append(item)
              .append(enclosing);
          prefix = ", ";
        }
        builder.append("]");
        return builder.toString();
      } else if (isStringCompatible(clazz)) {
        return "'" + expected + "'";
      }
      return expected.toString();
    }

    private boolean isStringCompatible(Class<?> clazz) {
      return clazz == String.class || clazz == StringBuilder.class || clazz == StringBuffer.class;
    }

    private String getEnclosing(Class<?> clazz) {
      String enclosing = "";
      if (isStringCompatible(clazz)) {
        enclosing = "'";
      }
      return enclosing;
    }
  }
}