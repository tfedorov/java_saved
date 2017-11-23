package com.tfedorov.social.web.service;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

@Service("httpProxyServiceImpl")
public class HttpProxyServiceImpl implements HttpProxyService {

  private static final String REPLACE_NUMBER = "[+]?\\d+";

  private final EtmMonitor performanceMonitor = EtmManager.getEtmMonitor();

  @Autowired
  @Qualifier("httpClientProxy")
  private HttpClient httpClient;


  @Override
  public String doGet(String url) throws IOException {
    if (url != null && !url.isEmpty()) {
      EtmPoint perfPoint = getPerformancePoint(" GET:[" + url.replaceAll(REPLACE_NUMBER, "*") + "]");
      try {
        HttpGet httpget = new HttpGet(url);
        return httpClient.execute(httpget, new BasicResponseHandler());
      } finally {
        perfPoint.collect();
      }
    }
    return "";
  }

  private EtmPoint getPerformancePoint(String name) {
    return performanceMonitor.createPoint(new StringBuilder(this.getClass().toString())
        .append(name).toString());
  }
}
