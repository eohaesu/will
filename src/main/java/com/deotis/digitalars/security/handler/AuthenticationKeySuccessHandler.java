package com.deotis.digitalars.security.handler;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.security.config.SecurityConstants;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description authentication success handler
 * 
**/
@RequiredArgsConstructor
@Slf4j
public class AuthenticationKeySuccessHandler implements AuthenticationSuccessHandler {

    @Value("${digitalars.default.site}")
	private String DEFAULT_SITE_CODE;
    
    @Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;

	
	@Override	
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		//set session registry ( spring session redis 미사용시 주석 해제)
		//sessionRegistry.registerNewSession(request.getSession().getId(), authentication.getPrincipal());
		
		SecretEntity secretEntity = SessionHandler.getSecretEntity();

		Map<String, String> userData = getParseUserData(secretEntity.getUserData());
		
		String ctiConnId = (userData != null && userData.get("ctiConnId") != null) ? userData.get("ctiConnId").toString() : "";

		//make base user entity
		UserEntity userEntity = new UserEntity(
						authentication.getName(),
						!DEFAULT_SITE_CODE.isEmpty() ? DEFAULT_SITE_CODE : secretEntity.getSiteCode(),
						secretEntity.getWmsAccessDeviceCode(),
						(userData != null && userData.get("customerName") != null) ? userData.get("customerName").toString() : "",
						(userData != null && userData.get("memberType") != null) ? userData.get("memberType").toString() : "",
						(userData != null && userData.get("menuDiv") != null) ? userData.get("menuDiv").toString() : "",
						(userData != null && userData.get("cmenuDiv") != null) ? userData.get("cmenuDiv").toString() : "",
						(userData != null && userData.get("timeStatus") != null) ? userData.get("timeStatus").toString() : "1",
						ctiConnId
						);

		SessionHandler.setSessionEntity(userEntity);

		log.debug("Authorization Success info: {}", SessionHandler.getAuthentication().getPrincipal());
		log.debug("User Entity: {}", SessionHandler.getSessionEntity());
		
	
		response.sendRedirect(request.getContextPath()+SecurityConstants.AUTH_SUCCESS_URI);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> getParseUserData(String userData) {

		Map<String, String> returnMap = null;
		
		if(StringUtils.hasLength(userData)) {

			try {
				returnMap = new ObjectMapper().readValue(userData, Map.class);
			} catch (JsonProcessingException e) {
				log.warn("Can not parse UserData. Is UserData construct with json?");
				return null;
			}
			
			log.info("Parsed UserData : {}", returnMap);
		}
		
		return returnMap;
	}
}
