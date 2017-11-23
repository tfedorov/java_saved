package com.tfedorov.social.web.performance;

import javax.servlet.ServletException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;



import etm.contrib.integration.spring.web.SpringEtmMonitorContextSupport;
import etm.core.monitor.EtmMonitor;

public class SpringHttpRequestPerformanceFilter extends HttpRequestPerformanceFilter {

  @Override
  protected EtmMonitor getEtmMonitor() throws ServletException {
    // retrieve name of EtmMonitor to use. may be null
    String etmMonitorName =
        filterConfig.getInitParameter(SpringEtmMonitorContextSupport.ETM_MONITOR_PARAMETER_NAME);
    WebApplicationContext ctx =
        WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig
            .getServletContext());

    return SpringEtmMonitorContextSupport.locateEtmMonitor(ctx, etmMonitorName);
  }
}
