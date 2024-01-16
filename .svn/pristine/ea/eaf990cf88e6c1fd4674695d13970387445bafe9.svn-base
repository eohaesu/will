package com.deotis.digitalars.system.Interceptor;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description intercept API Client 
 */
@Slf4j
@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		
		//prehandle
		logRequest(request, body);
		
		ClientHttpResponse response = execution.execute(request, body);
		
		//posthandle
		logResponse(response);
		
		return response;
	}
	
	private void logRequest(HttpRequest request, byte[] body) throws IOException 
    {
		log.info("======= External RestTemplate API request begin =======");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        log.info("Request body: {}", new String(body, "UTF-8"));
        log.info("======= External RestTemplate API request end =======");
    }
  
    private void logResponse(ClientHttpResponse response) throws IOException 
    {
    	 log.info("======= External RestTemplate API response begin =======");
         log.info("Status code  : {}", response.getStatusCode());
         log.info("Status text  : {}", response.getStatusText());
         log.info("Headers      : {}", response.getHeaders());
         log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
         log.info("======= External RestTemplate API response end =======");
    }
	
}