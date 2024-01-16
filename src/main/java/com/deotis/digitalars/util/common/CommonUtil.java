package com.deotis.digitalars.util.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

/**
 * 
 * @author jongjin
 * @description common static utility
 */

public class CommonUtil {
	
	private final static String LOCALHOST_IPV4 = "127.0.0.1";
	private final static String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

	// 이름 가운데 글자 마스킹
	public static String nameMasking(String name) {

		// 한글 + 숫자
		String regex = "(^[가-힣0-9]+)$";

		Matcher matcher = Pattern.compile(regex).matcher(name);
		if (matcher.find()) {

			int length = name.length();

			String middleMask = "";
			if (length > 2) {
				middleMask = name.substring(1, length - 1);
			} else {
				middleMask = name.substring(1, length);
			}

			String dot = "";
			for (int i = 0; i < middleMask.length(); i++) {
				dot += "*";
			}

			if (length > 2) {
				return name.substring(0, 1) + middleMask.replace(middleMask, dot) + name.substring(length - 1, length);

				// 2글자 이름
			} else {
				return name.substring(0, 1) + middleMask.replace(middleMask, dot);
			}

		}

		return name;

	}

	// 휴대폰번호 마스킹(가운데 숫자 4자리 마스킹)
	public static String phoneMasking(String phoneNo) {

		String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";

		Matcher matcher = Pattern.compile(regex).matcher(phoneNo);
		if (matcher.find()) {

			String target = matcher.group(2);
			int length = target.length();
			char[] c = new char[length];
			Arrays.fill(c, '*');

			return phoneNo.replace(target, String.valueOf(c));

		}

		return phoneNo;

	}

	// XSS 특수문자 치환
	public static String XssEscape(String param) {

		param = param.replaceAll("<", "&lt;");
		param = param.replaceAll(">", "&gt;");
		param = param.replaceAll("&", "%amp;");
		param = param.replaceAll("\"", "&#quot;");
		param = param.replaceAll("'", "&#x27;");
		
		param = param.replaceAll("/", "&#x2F;");
		param = param.replaceAll("\\(", "&#x28;");

		return param;

	}

	// XSS 특수문자 치환
	public static String XssUnEscape(String param) {

		param = param.replaceAll("&lt;", "<");
		param = param.replaceAll("&gt;", ">");
		param = param.replaceAll("%amp;", "&");
		param = param.replaceAll("&#quot;", "\"");
		param = param.replaceAll("&#x27;", "'");
		
		param = param.replaceAll("&#x2F;", "/");
		param = param.replaceAll("&#x28;", "(");

		return param;

	}
	
	public static String getClientIp(HttpServletRequest requset) throws UnknownHostException  {
	
		String ipAddress = requset.getHeader("X-Forwarded-For");
		
		
		if(!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress) ){
			ipAddress = requset.getHeader("Proxy-Client-IP");			
		}
		
		if(!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress) ){
			ipAddress = requset.getHeader("WL-Proxy-Client-IP");			
		}
		
		if(!StringUtils.hasLength(ipAddress) || "unknown".equalsIgnoreCase(ipAddress) ){
			ipAddress = requset.getRemoteAddr();
			if( LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress) ) {
				InetAddress inetAddress = InetAddress.getLocalHost();
				ipAddress = inetAddress.getHostAddress();
			}
		}
		
		if(StringUtils.hasLength(ipAddress)
				&& ipAddress.length() > 15
				&& ipAddress.indexOf(",") > 0) {		
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
		}
		
		return ipAddress;
		
	}		
	
}