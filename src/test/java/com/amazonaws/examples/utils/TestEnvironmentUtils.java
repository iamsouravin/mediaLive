package com.amazonaws.examples.utils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

public class TestEnvironmentUtils {

  @SuppressWarnings("unchecked")
  public static void setEnv(Map<String, String> newEnv) throws Exception {
    try {
      Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
      env.putAll(newEnv);
      @SuppressWarnings("JavaReflectionMemberAccess") Field theCaseInsensitiveEnvironmentField =
          processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true);
      Map<String, String> cienv =
          (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
      cienv.putAll(newEnv);
    } catch (NoSuchFieldException e) {
      Class<?>[] classes = Collections.class.getDeclaredClasses();
      Map<String, String> env = System.getenv();
      for (Class<?> cl : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          Map<String, String> map = (Map<String, String>) field.get(env);
          map.putAll(newEnv);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static void removeEnv(Map<String, String> newEnv) throws Exception {
    try {
      Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
      newEnv.keySet().forEach(env::remove);
      @SuppressWarnings("JavaReflectionMemberAccess") Field theCaseInsensitiveEnvironmentField =
          processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true);
      Map<String, String> cienv =
          (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
      newEnv.keySet().forEach(cienv::remove);
    } catch (NoSuchFieldException e) {
      Class<?>[] classes = Collections.class.getDeclaredClasses();
      Map<String, String> env = System.getenv();
      for (Class<?> cl : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          Map<String, String> map = (Map<String, String>) field.get(env);
          newEnv.keySet().forEach(map::remove);
        }
      }
    }
  }
}
