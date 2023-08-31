package com.glenncai.openbiplatform.config.xss;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * Xss filter class, used to filter malicious characters in the request
 *
 * @author Glenn Cai
 * @version 1.0 31/08/2023
 */
@WebFilter(urlPatterns = "/*")
public class XssFilter implements Filter {
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Filter.super.init(filterConfig);
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    XssHttpServletRequestWrapper wrapper = new XssHttpServletRequestWrapper(request);
    filterChain.doFilter(wrapper, servletResponse);
  }

  @Override
  public void destroy() {
    Filter.super.destroy();
  }
}
