package com.glenncai.openbiplatform.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is for Excel utils
 *
 * @author Glenn Cai
 * @version 1.0 07/25/2023
 */
@Slf4j
public class ExcelUtils {

  private static final String xlxMimeType = "application/vnd.ms-excel";

  private static final String xlsxMimeType =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  /**
   * Convert Excel to CSV
   *
   * @param multipartFile Excel file
   * @return CSV string
   */
  public static String excelToCsv(MultipartFile multipartFile) {
    String mimeType = multipartFile.getContentType();
    ExcelTypeEnum excelType = null;

    switch (Objects.requireNonNull(mimeType)) {
      case xlxMimeType -> excelType = ExcelTypeEnum.XLS;
      case xlsxMimeType -> excelType = ExcelTypeEnum.XLSX;
    }

    List<Map<Integer, String>> list = null;
    try {
      list = EasyExcelFactory.read(multipartFile.getInputStream())
                             .excelType(excelType)
                             .sheet()
                             .headRowNumber(0)
                             .doReadSync();
    } catch (IOException e) {
      log.error("Read Excel file failed. Input file name: {}, mime type: {}",
                multipartFile.getOriginalFilename(), mimeType);
    }

    if (CollectionUtils.isEmpty(list)) {
      return "";
    }

    StringBuilder stringBuilder = new StringBuilder();
    // Read header
    LinkedHashMap<Integer, String> headerMap = (LinkedHashMap<Integer, String>) list.get(0);
    // Convert header to list
    List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).toList();
    // Append header to CSV string
    stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
    // Read data
    for (int i = 1; i < list.size(); i++) {
      LinkedHashMap<Integer, String> dataMap = (LinkedHashMap<Integer, String>) list.get(i);
      List<String> dataList = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).toList();
      stringBuilder.append(StringUtils.join(dataList, ",")).append("\n");
    }

    return stringBuilder.toString();
  }
}
