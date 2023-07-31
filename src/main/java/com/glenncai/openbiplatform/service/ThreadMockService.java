package com.glenncai.openbiplatform.service;

import com.glenncai.openbiplatform.model.dto.thread_mock.request.ThreadMockAddRequest;

/**
 * Thread mock service in order to test thread pool in local
 *
 * @author Glenn Cai
 * @version 1.0 07/31/2023
 */
public interface ThreadMockService {

  /**
   * Add admin AI task to thread pool
   *
   * @param threadMockAddRequest request body
   */
  void addAdminAiTask(ThreadMockAddRequest threadMockAddRequest);

  /**
   * Add user AI task to thread pool
   *
   * @param threadMockAddRequest request body
   */
  void addUserAiTask(ThreadMockAddRequest threadMockAddRequest);

  /**
   * Get admin AI thread pool info
   *
   * @return admin AI thread pool info
   */
  String getAdminAiThreadPoolInfo();

  /**
   * Get user AI thread pool info
   *
   * @return user AI thread pool info
   */
  String getUserAiThreadPoolInfo();
}
