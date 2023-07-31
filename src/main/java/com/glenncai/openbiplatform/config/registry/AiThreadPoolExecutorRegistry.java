package com.glenncai.openbiplatform.config.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * AI thread pool registry
 *
 * @author Glenn Cai
 * @version 1.0 07/31/2023
 */
public class AiThreadPoolExecutorRegistry {

  private static final Map<String, ThreadPoolExecutor> aiThreadPoolExecutorMap =
      new ConcurrentHashMap<>();

  private AiThreadPoolExecutorRegistry() {
  }

  /**
   * Get AI thread pool executor by role
   *
   * @param role user role
   * @return thread pool executor
   */
  public static ThreadPoolExecutor getAiThreadPoolExecutor(String role) {
    return aiThreadPoolExecutorMap.get(role);
  }

  /**
   * Register AI thread pool executor by role
   *
   * @param role               user role
   * @param threadPoolExecutor thread pool executor
   */
  public static void registerAiThreadPoolExecutor(String role,
                                                  ThreadPoolExecutor threadPoolExecutor) {
    aiThreadPoolExecutorMap.put(role, threadPoolExecutor);
  }
}
