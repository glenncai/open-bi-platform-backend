package com.glenncai.openbiplatform.config.xss;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Custom request wrapper class, used to filter malicious characters in the request.
 * It will remove the harmful HTML tags.
 *
 * @author Glenn Cai
 * @version 1.0 31/08/2023
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

  public XssHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String getParameter(String name) {
    String value = super.getParameter(name);
    if (!CharSequenceUtil.hasEmpty(value)) {
      value = HtmlUtil.cleanHtmlTag(value);
    }
    return value;
  }

  @Override
  public String[] getParameterValues(String name) {
    String[] values = super.getParameterValues(name);
    if (values != null) {
      for (int i = 0; i < values.length; i++) {
        String value = values[i];
        if (!CharSequenceUtil.hasEmpty(value)) {
          value = HtmlUtil.cleanHtmlTag(value);
        }
        values[i] = value;
      }
    }
    return values;
  }

  @Override
  public String getHeader(String name) {
    String value = super.getHeader(name);
    if (!CharSequenceUtil.hasEmpty(value)) {
      value = HtmlUtil.cleanHtmlTag(value);
    }
    return value;
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    Map<String, String[]> parameterMap = super.getParameterMap();
    LinkedHashMap<String, String[]> map = new LinkedHashMap<>();
    if (parameterMap != null) {
      for (Map.Entry<String, String[]> key : parameterMap.entrySet()) {
        String[] values = key.getValue();
        if (values != null) {
          for (int i = 0; i < values.length; i++) {
            String value = values[i];
            if (!CharSequenceUtil.hasEmpty(value)) {
              value = HtmlUtil.cleanHtmlTag(value);
            }
            values[i] = value;
          }
        }
        map.put(String.valueOf(key), values);
      }
    }
    return map;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    InputStream in = super.getInputStream();
    InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
    BufferedReader buffer = new BufferedReader(reader);
    StringBuilder body = new StringBuilder();
    String line = buffer.readLine();
    while (line != null) {
      body.append(line);
      line = buffer.readLine();
    }
    buffer.close();
    reader.close();
    in.close();

    Map<String, Object> map = JSONUtil.parseObj(body.toString());
    Map<String, Object> result = new LinkedHashMap<>();
    for (Map.Entry<String, Object> key : map.entrySet()) {
      Object val = key.getValue();
      if (val instanceof String) {
        if (!CharSequenceUtil.hasEmpty(val.toString())) {
          result.put(String.valueOf(key), HtmlUtil.cleanHtmlTag(val.toString()));
        }
      } else {
        result.put(String.valueOf(key), val);
      }
    }
    String json = JSONUtil.toJsonStr(result);
    ByteArrayInputStream bain = new ByteArrayInputStream(json.getBytes());

    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {

      }

      @Override
      public int read() throws IOException {
        return bain.read();
      }
    };
  }
}
