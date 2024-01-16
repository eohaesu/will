package com.deotis.digitalars.system.Interceptor;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.system.exception.InternalRestEnum;
import com.deotis.digitalars.util.common.CommonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description intercept Internal API Client 
 */
@Slf4j
public class InternalRestInterceptor implements HandlerInterceptor {
	
	@Value("${rest.internal.auth.use}")
	private boolean REST_INTERNAL_AUTH_USE;
	
	@Value("${rest.internal.allow.ip}")
	private String[] REST_INTERNAL_ALLOW_IP;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException{
		
		String uriPath = new UrlPathHelper().getPathWithinApplication(request);
		String remoteDiv = uriPath.contains("/wms/") ? "WMS" : "EXTERNAL";
		String clientIp = CommonUtil.getClientIp(request);
		
		log.info("======= Internal Rest API request from {} IP:{} =======", remoteDiv, clientIp);

		if(REST_INTERNAL_AUTH_USE) {
			
			if("EXTERNAL".equals(remoteDiv)) {
				
				log.info(" [ Request Menu Url : {} ]", request.getRequestURI());
				log.info(" [ Request Remorte Ip : {} ]", request.getRemoteAddr());

				String transactionCode = request.getHeader(CommonConstants.API_TRANSACTION_CODE);
				String authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION);
				
				log.info("internal rest process transactionCode:[{}], authorizationToken:[{}]", transactionCode, authorizationToken );
				
				//if(!StringUtils.hasLength(transactionCode)|| !StringUtils.hasLength(authorizationToken)|| !verifyBasicToken(authorizationToken)) {
				if(!StringUtils.hasLength(transactionCode)) { 
					log.info(" [External Request check header Authorization has been fail : {} ]", clientIp);
					response.sendRedirect(request.getContextPath()+"/api/exception/internal/"+InternalRestEnum.INVALID_AUTHORIZATION);
					
					return false;
				}
			}
			//WMS Confirm IP address
			if("WMS".equals(remoteDiv) && !Arrays.stream(REST_INTERNAL_ALLOW_IP).anyMatch(clientIp::equals)) {
				log.info(" [ Internal WMS check allow IP Authorization has been fail : {} ]", clientIp);
				response.sendRedirect(request.getContextPath()+"/api/exception/internal/"+InternalRestEnum.NOT_ALLOW);
				return false;
			}

		}

		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView){

		
	}
	
	/* rest api authentication
	private boolean verifyBasicToken(String token) {
		
		String[] split = token.split(" ");
        String type = split[0];
        String credential = split[1];

        if ("Basic".equalsIgnoreCase(type)) {

            String decoded = new String(Base64Utils.decodeFromString(credential));
            
            String[] nameValue = decoded.split(":");

            if(CommonConstants.BASIC_AUTH_NAME.equals(nameValue[0]) && CommonConstants.BASIC_AUTH_VALUE.equals(nameValue[1])) {
            	return true;
            }
        }
        
        return false;
	}
	*/

}