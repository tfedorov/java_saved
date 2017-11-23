package com.tfedorov.social.web.service;

import java.io.IOException;

public interface HttpProxyService {

  String doGet(final String url) throws IOException;
}
