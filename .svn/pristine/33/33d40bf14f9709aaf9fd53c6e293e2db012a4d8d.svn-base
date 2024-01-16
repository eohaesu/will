package com.deotis.digitalars.system.rest.client;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.deotis.digitalars.constants.CommonConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description block rest connector extends RestTemplate
 */

@Slf4j
public class DeotisTemplate extends RestTemplate{

	private final String REST_API_TRANSACTION_CODE = CommonConstants.API_TRANSACTION_CODE;
	
	public DeotisTemplate(HttpComponentsClientHttpRequestFactory requestFactory, List<ClientHttpRequestInterceptor> interceptors){

		super(new BufferingClientHttpRequestFactory(requestFactory));
		super.setInterceptors(interceptors);

	}
	//default header configuration
	public HttpEntity<?> getHttpEntityDefaultHeader(HttpEntity<?> requestEntity){
		
		if(requestEntity!=null){
			
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.putAll(requestEntity.getHeaders());
			//headers.set(REST_API_TRANSACTION_CODE, RequestContextHolder.getRequestAttributes().getSessionId());
			headers.set(REST_API_TRANSACTION_CODE, RandomStringUtils.randomAlphanumeric(10));

			return new HttpEntity<>(requestEntity.getBody(), headers);
		}

		HttpHeaders headers = new HttpHeaders();
		
		headers.add(REST_API_TRANSACTION_CODE, RandomStringUtils.randomAlphanumeric(10));
		
		//if need to header authorize todo
		//headers.add("Authorization", createHeaders(CommonConstants.BASIC_AUTH_USERNAME, CommonConstants.BASIC_AUTH_PASSWORD));

		HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		return httpEntity;
	}
	
	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		log.info("REST_API_TRANSACTION_CODE {}",requestEntity.getHeaders().get(REST_API_TRANSACTION_CODE));
		
		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		log.info("REST_API_TRANSACTION_CODE {}",requestEntity.getHeaders().get(REST_API_TRANSACTION_CODE));
		
		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		log.info("REST_API_TRANSACTION_CODE {}",requestEntity.getHeaders().get(REST_API_TRANSACTION_CODE));
		
		return super.exchange(url, method, requestEntity, responseType);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Object... uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);
		
		log.info("REST_API_TRANSACTION_CODE {}",requestEntity.getHeaders().get(REST_API_TRANSACTION_CODE));
		
		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		
		requestEntity = getHttpEntityDefaultHeader(requestEntity);

		log.info("REST_API_TRANSACTION_CODE {}",requestEntity.getHeaders().get(REST_API_TRANSACTION_CODE));

		return super.exchange(url, method, requestEntity, responseType, uriVariables);
	}
	/* If use rest authentication
	private String createHeaders(String username, String password){
		 String auth = username + ":" + password;
         byte[] encodedAuth = Base64.encodeBase64( 
         auth.getBytes(Charset.forName("UTF-8")) );
         return "Basic " + new String( encodedAuth );
	}
	*/
}