package com.deotis.digitalars.controller.business;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.constants.IVRConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.WmsRequestEntity;
import com.deotis.digitalars.model.WmsResponseEntity;
import com.deotis.digitalars.service.rest.external.WmsExternalService;
import com.deotis.digitalars.system.exception.WmsException;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.util.common.RegexUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/ivr")
@Controller
public class IvrBusinessController {
	
	@Value("${ivr.interface.json.gotoend}")
	private boolean GOTOEND_JSON;
	
	private final WmsExternalService wmsService;

	/**
	 * SMS 전송 view
	 * @return String
	 */
	@GetMapping(value="/send/sms")
	public String sendSms() {
		return "contents/business/sendSms";
	}
	
	/**
	 * SMS 전송
	 * @param String
	 * @return Integer
	 */
	@PostMapping(value="/send/sms/etc")
	@ResponseBody
	public Integer etcSms(
			@RequestParam(value = "cell", defaultValue = "") String cell,
			@RequestParam(value = "scCode", defaultValue = "") String scCode
			) {

		UserEntity entity = SessionHandler.getSessionEntity();
		
		try {
			
			if(!StringUtils.hasLength(cell)) {
				cell = SessionHandler.getSecretEntity().getAni();
			}
			
			if(!RegexUtil.patternValidate(cell, RegexUtil.Patterns.CELL)) {
				return -1;
			}
			
			cell = cell.replaceAll("-", "");
			
			entity.setShowArsProgressType(1);
			
			WmsRequestEntity params = new WmsRequestEntity();
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
			
			if(GOTOEND_JSON) {
				JSONObject messageObj = new JSONObject();
				
				messageObj.put("key", scCode);
				messageObj.put("value", cell);
				
				params.setMessage(messageObj.toString());
			}else {
				StringBuilder messageObj = new StringBuilder();
				
				messageObj.append(IVRConstants.GOTOEND_SMS);
				messageObj.append(IVRConstants.SEPARATOR);
				messageObj.append(scCode);
				messageObj.append(IVRConstants.SEPARATOR);
				messageObj.append(cell);
				
				params.setMessage(messageObj.toString());
			}

			log.debug("#SEND SMS. WMS External goToEnd parameters : {}", params);
			
			WmsResponseEntity result = wmsService.goToEnd(params);
						
			if(result.getResult() != null && "0".equals(result.getResult())) {
				return 1;
			}else {
				entity.setShowArsProgressType(0);
				return -2;
			}

		}catch (WmsException e) {
			entity.setShowArsProgressType(0);
			if(log.isDebugEnabled()){
				log.error("Error : #SEND SMS. Go to End Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			}
		}
		
		return -2;
	}

	/**
	 * Email 전송 view
	 * @return String
	 */
	@GetMapping(value="/send/email")
	public String sendEmail() {
		return "contents/business/sendEmail";
	}
	
	/**
	 * Email 전송
	 * @param String
	 * @return Integer
	 */
	@PostMapping(value="/send/mail/etc")
	@ResponseBody
	public Integer etcEmail(
			@RequestParam(value = "email", defaultValue = "") String email,
			@RequestParam(value = "scCode", defaultValue = "") String scCode
			) {
		
		UserEntity entity = SessionHandler.getSessionEntity();

		try {
			
			if(!RegexUtil.patternValidate(email, RegexUtil.Patterns.EMAIL)) {
				return -1;
			}
			
			entity.setShowArsProgressType(1);
			
			WmsRequestEntity params = new WmsRequestEntity();
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());

			if(GOTOEND_JSON) {
				JSONObject messageObj = new JSONObject();
				
				messageObj.put("key", scCode);
				messageObj.put("value", email);
				
				params.setMessage(messageObj.toString());
			}else {
				StringBuilder messageObj = new StringBuilder();
				
				messageObj.append(IVRConstants.GOTOEND_EMAIL);
				messageObj.append(IVRConstants.SEPARATOR);
				messageObj.append(scCode);
				messageObj.append(IVRConstants.SEPARATOR);
				messageObj.append(email);
				
				params.setMessage(messageObj.toString());
			}

			log.debug("#SEND E-MAIL. WMS External goToEnd parameters : {}", params);
			
			WmsResponseEntity result = wmsService.goToEnd(params);
						
			if(result.getResult() != null && "0".equals(result.getResult())) {
				return 1;
			}else {
				entity.setShowArsProgressType(0);
				return -2;
			}

		}catch (WmsException e) {
			entity.setShowArsProgressType(0);
			if(log.isDebugEnabled()){
				log.error("Error : #SEND E-MAIL. Go to End Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			}
		}
		
		return -2;
	}
	
	/**
	 * FAX발송 view
	 * @return String
	 */
	@GetMapping(value="/send/fax")
	public String sendFax() {
		return "contents/business/sendFax";
	}
	

	/**
	 * Fax 전송
	 * @param String
	 * @return Integer
	 */
	@PostMapping(value="/send/fax/etc")
	@ResponseBody
	public Integer etcFax(
			@RequestParam(value = "fax", defaultValue = "") String fax,
			@RequestParam(value = "scCode", defaultValue = "") String scCode
			) {
		
		UserEntity entity = SessionHandler.getSessionEntity();

		try {

			if(!RegexUtil.patternValidate(fax, RegexUtil.Patterns.FAX)) {
				return -1;
			}
			
			fax = fax.replaceAll("-", "");

			entity.setShowArsProgressType(1);
			
			WmsRequestEntity params = new WmsRequestEntity();
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
			
			if(GOTOEND_JSON) {
				JSONObject messageObj = new JSONObject();
				
				messageObj.put("key", scCode);
				messageObj.put("value", fax);
				
				params.setMessage(messageObj.toString());
			}else {
				StringBuilder messageObj = new StringBuilder();
				
				messageObj.append(IVRConstants.GOTOEND_FAX);
				messageObj.append(IVRConstants.SEPARATOR);
				messageObj.append(scCode);
				messageObj.append(IVRConstants.SEPARATOR);
				messageObj.append(fax);
				
				params.setMessage(messageObj.toString());
			}
			
			log.debug("#SEND FAX. WMS External goToEnd parameters : {}", params);
			
			WmsResponseEntity result = wmsService.goToEnd(params);
						
			if(result.getResult() != null && "0".equals(result.getResult())) {
				return 1;
			}else {
				entity.setShowArsProgressType(0);
				return -2;
			}

		}catch (WmsException e) {
			entity.setShowArsProgressType(0);
			if(log.isDebugEnabled()){
				log.error("Error : #SEND FAX. Go to End Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			}
		}
		
		return -2;
	}
	
	
	/**
	 * callback view
	 * @return String
	 */
	@GetMapping(value="/send/callback")
	public String callback() {
		return "contents/business/callBack";
	}
	
	/**
	 * callback 전송
	 * @param String
	 * @return Integer
	 */
	@PostMapping(value="/send/callback/etc")
	@ResponseBody
	public Integer etcCallback(@RequestParam(value = "tel", defaultValue = "") String tel) {
		
		UserEntity entity = SessionHandler.getSessionEntity();

		try {
			
			if(!StringUtils.hasLength(tel)) {
				tel = SessionHandler.getSecretEntity().getAni();
			}

			if(!RegexUtil.patternValidate(tel, RegexUtil.Patterns.CELL) && !RegexUtil.patternValidate(tel, RegexUtil.Patterns.FAX)) {
				return -1;
			}
			
			tel = tel.replaceAll("-", "");

			entity.setShowArsProgressType(1);
			
			WmsRequestEntity params = new WmsRequestEntity();
			params.setSid(entity.getSid());
			params.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());

			if(GOTOEND_JSON) {
				
				JSONObject messageObj = new JSONObject();
				
				messageObj.put("key", CommonConstants.IVR_CALLBACK_SERVICE);
				messageObj.put("value", tel);
				
				params.setMessage(messageObj.toString());
			}else {
				StringBuilder messageObj = new StringBuilder();
				
				messageObj.append(IVRConstants.GOTOEND_CALLBACK);
				messageObj.append(IVRConstants.SEPARATOR);
				messageObj.append(tel);
				
				params.setMessage(messageObj.toString());
			}
			
			log.debug("#SEND CALLBACK SERVICE. WMS External goToEnd parameters : {}", params);
			
			WmsResponseEntity result = wmsService.goToEnd(params);
						
			if(result.getResult() != null && "0".equals(result.getResult())) {
				return 1;
			}else {
				entity.setShowArsProgressType(0);
				return -2;
			}

		}catch (WmsException e) {
			entity.setShowArsProgressType(0);
			if(log.isDebugEnabled()){
				log.error("Error : #SEND CALLBACK SERVICE. Go to End Failed USID : [{}], WMSValue : [{}]", e.getSid(), e.getWmsValue());
			}
		}
		
		return -2;
	}
	
	/**
	 * finish view
	 * @return String
	 */
	@GetMapping(value="/send/finish")
	public String finish() {
		return "contents/business/sendFinish";
	}
	
	/**
	 * fail view
	 * @return String
	 */
	@GetMapping(value="/send/fail")
	public String fail() {
		return "contents/business/sendFail";
	}
}