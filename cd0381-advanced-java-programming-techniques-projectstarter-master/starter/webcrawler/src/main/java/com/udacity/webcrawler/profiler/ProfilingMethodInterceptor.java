package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Object delegate;
  private final ProfilingState state;
  private final Clock clock;

  ProfilingMethodInterceptor(Object delegate, ProfilingState state, Clock clock) {
    this.delegate = Objects.requireNonNull(delegate);
    this.state = Objects.requireNonNull(state);
    this.clock = Objects.requireNonNull(clock);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

    // Handle Object methods normally
    if (method.getDeclaringClass() == Object.class) {
      return method.invoke(delegate, args);
    }

    // Check annotation on interface method
    boolean isProfiled = method.isAnnotationPresent(Profiled.class);

    // ALSO check annotation on implementation method
    if (!isProfiled) {
      try {
        Method implMethod =
            delegate.getClass()
                .getMethod(method.getName(), method.getParameterTypes());

        isProfiled = implMethod.isAnnotationPresent(Profiled.class);
      } catch (NoSuchMethodException ignored) {
      }
    }

    if (!isProfiled) {
      try {
        return method.invoke(delegate, args);
      } catch (InvocationTargetException e) {
        throw e.getCause();
      }
    }

    Instant start = clock.instant();

    try {
      return method.invoke(delegate, args);
    } catch (InvocationTargetException e) {
      throw e.getCause();
    } finally {
      Instant end = clock.instant();
      Duration duration = Duration.between(start, end);

      // IMPORTANT: record using implementation class
      state.record(delegate.getClass(), method, duration);
    }
  }
}