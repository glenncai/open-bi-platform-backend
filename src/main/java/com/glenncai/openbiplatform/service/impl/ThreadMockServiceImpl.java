package com.glenncai.openbiplatform.service.impl;

import cn.hutool.json.JSONUtil;
import com.glenncai.openbiplatform.common.ErrorCode;
import com.glenncai.openbiplatform.config.registry.AiThreadPoolExecutorRegistry;
import com.glenncai.openbiplatform.constant.UserConstant;
import com.glenncai.openbiplatform.exception.BusinessException;
import com.glenncai.openbiplatform.model.dto.thread_mock.request.ThreadMockAddRequest;
import com.glenncai.openbiplatform.service.ThreadMockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread mock service implementation
 *
 * @author Glenn Cai
 * @version 1.0 07/31/2023
 */
@Service
@Slf4j
public class ThreadMockServiceImpl implements ThreadMockService {

  /**
   * Add admin AI task to thread pool
   *
   * @param threadMockAddRequest request body
   */
  @Override
  public void addAdminAiTask(ThreadMockAddRequest threadMockAddRequest) {
    String role = UserConstant.ADMIN_ROLE;
    String name = threadMockAddRequest.getName();

    CompletableFuture.runAsync(() -> {
      log.info("The task {} is running in thread {}", name, Thread.currentThread().getName());
      try {
        Thread.sleep(30000); // 30s
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new BusinessException(ErrorCode.OPERATION_ERROR.getCode(), e.getMessage());
      }
    }, AiThreadPoolExecutorRegistry.getAiThreadPoolExecutor(role));
  }

  /**
   * Add user AI task to thread pool
   *
   * @param threadMockAddRequest request body
   */
  @Override
  public void addUserAiTask(ThreadMockAddRequest threadMockAddRequest) {
    String role = UserConstant.DEFAULT_ROLE;
    String name = threadMockAddRequest.getName();

    CompletableFuture.runAsync(() -> {
      log.info("The task {} is running in thread {}", name, Thread.currentThread().getName());
      try {
        Thread.sleep(30000); // 30s
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new BusinessException(ErrorCode.OPERATION_ERROR.getCode(), e.getMessage());
      }
    }, AiThreadPoolExecutorRegistry.getAiThreadPoolExecutor(role));
  }

  /**
   * Get admin AI thread pool info
   *
   * @return admin AI thread pool info
   */
  @Override
  public String getAdminAiThreadPoolInfo() {
    ThreadPoolExecutor threadPoolExecutor = AiThreadPoolExecutorRegistry.getAiThreadPoolExecutor(
        UserConstant.ADMIN_ROLE);

    Map<String, Object> map = new HashMap<>();
    int size = threadPoolExecutor.getQueue().size();
    map.put("Queue size:", size);
    long taskCount = threadPoolExecutor.getTaskCount();
    map.put("Task count:", taskCount);
    long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
    map.put("Completed task count:", completedTaskCount);
    long activeCount = threadPoolExecutor.getActiveCount();
    map.put("Active count:", activeCount);

    return JSONUtil.toJsonStr(map);
  }

  /**
   * Get user AI thread pool info
   *
   * @return user AI thread pool info
   */
  @Override
  public String getUserAiThreadPoolInfo() {
    ThreadPoolExecutor threadPoolExecutor = AiThreadPoolExecutorRegistry.getAiThreadPoolExecutor(
        UserConstant.DEFAULT_ROLE);

    Map<String, Object> map = new HashMap<>();
    int size = threadPoolExecutor.getQueue().size();
    map.put("Queue size:", size);
    long taskCount = threadPoolExecutor.getTaskCount();
    map.put("Task count:", taskCount);
    long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
    map.put("Completed task count:", completedTaskCount);
    long activeCount = threadPoolExecutor.getActiveCount();
    map.put("Active count:", activeCount);

    return JSONUtil.toJsonStr(map);
  }
}
