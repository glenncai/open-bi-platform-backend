package com.glenncai.openbiplatform.controller;

import com.glenncai.openbiplatform.model.dto.thread_mock.request.ThreadMockAddRequest;
import com.glenncai.openbiplatform.service.ThreadMockService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * This class is for thread mock controller (API)
 *
 * @author Glenn Cai
 * @version 1.0 07/31/2023
 */
@Api(tags = "Thread Mock Controller")
@RestController
@RequestMapping("/thread")
@Profile({"dev", "test"})
@Slf4j
public class ThreadMockController {

  @Resource
  private ThreadMockService threadMockService;

  @PostMapping("/ai/admin")
  public void addAdminAiTask(@RequestBody ThreadMockAddRequest threadMockAddRequest) {
    threadMockService.addAdminAiTask(threadMockAddRequest);
  }

  @PostMapping("/ai/user")
  public void addUserAiTask(@RequestBody ThreadMockAddRequest threadMockAddRequest) {
    threadMockService.addUserAiTask(threadMockAddRequest);
  }

  @GetMapping("/ai/admin")
  public void getAdminAiThreadInfo() {
    String threadPoolInfo = threadMockService.getAdminAiThreadPoolInfo();
    log.info(threadPoolInfo);
  }

  @GetMapping("/ai/user")
  public void getUserAiThreadInfo() {
    String threadPoolInfo = threadMockService.getUserAiThreadPoolInfo();
    log.info(threadPoolInfo);
  }
}
