package com.glenncai.openbiplatform.config;

import com.glenncai.openbiplatform.config.registry.AiThreadPoolExecutorRegistry;
import com.glenncai.openbiplatform.constant.UserConstant;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * AI service role base thread pool executor configuration
 *
 * @author Glenn Cai
 * @version 1.0 07/30/2023
 */
@Configuration
public class AiThreadPoolExecutorConfig {

  /**
   * Create admin thread pool executor
   *
   * @return admin thread pool executor
   */
  @Bean
  public ThreadPoolExecutor adminThreadPoolExecutor() {
    ThreadFactory threadFactory = new ThreadFactory() {
      private int count = 0;

      @Override
      public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("admin-ai-thread-" + count);
        count++;
        return thread;
      }
    };

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        2,
        2,
        60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10),
        threadFactory
    );

    AiThreadPoolExecutorRegistry.registerAiThreadPoolExecutor(UserConstant.ADMIN_ROLE,
                                                              threadPoolExecutor);
    return threadPoolExecutor;
  }

  /**
   * Create user thread pool executor
   *
   * @return user thread pool executor
   */
  @Bean
  public ThreadPoolExecutor userThreadPoolExecutor() {
    ThreadFactory threadFactory = new ThreadFactory() {
      private int count = 0;

      @Override
      public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("user-ai-thread-" + count);
        count++;
        return thread;
      }
    };

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        2,
        4,
        60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(20),
        threadFactory
    );

    AiThreadPoolExecutorRegistry.registerAiThreadPoolExecutor(UserConstant.DEFAULT_ROLE,
                                                              threadPoolExecutor);
    return threadPoolExecutor;
  }
}
