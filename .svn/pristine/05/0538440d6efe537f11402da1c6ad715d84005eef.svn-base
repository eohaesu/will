package com.deotis.digitalars.controller.common;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deotis.digitalars.constants.CommonConstants;
import com.deotis.digitalars.model.TraceJourney;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.WmsRequestEntity;
import com.deotis.digitalars.model.WmsResponseEntity;
import com.deotis.digitalars.security.config.SecurityConstants;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.rest.external.WmsExternalService;
import com.deotis.digitalars.service.rest.internal.TraceJourneyService;
import com.deotis.digitalars.system.exception.WmsException;
import com.deotis.digitalars.system.handler.SessionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CommonController {

	private final TraceJourneyService traceJourneyService;
	//private final MessageSource messageSource; //support for multiple language
	
	private final WmsExternalService wmsService;

	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	/**
	 * bot 설정
	 * @return String
	 */
	@GetMapping("/robots.txt")
	@ResponseBody
    public String robots(){
		return "User-agent: *\nDisallow: /\n";
    }
	
	/**
	 * 기본 진입 페이지
	 * @param model
	 * @param session
	 * @param request
	 * @param response
	 * @return String
	 */
	@GetMapping(value={"", "/"})
	public String income(ModelMap model, @RequestParam(value = "crid", required = false) String crid) {
		if(!StringUtils.hasLength(crid)){
			return "redirect:/auth/accessDenied"; 
		}else {
			model.addAttribute("crid", crid);
			
			//미리 보기용 페이지로 이동
			return "contents/common/preview";
		}

	}

	/**
	 * 콜 종료 후 키패드 화면 전환
	 * 
	 * @return String
	 * @throws Exception 
	 */
	 
	@GetMapping(value = "/common/sessionEndWithParam")
	public String sessionEndWithTrace(
			@RequestParam(value = "pt", required = false, defaultValue = "") String pt,
			@RequestParam(value = "link", required = false, defaultValue = "false") boolean link
			){

		//종료페이지 변경시 파라미터 처리
		String endPointParam = !"".equals(pt) ? "?pt="+pt.replaceAll("[\\r\\n]", "") : "";
		
		//Call end 이후 딥링크 실행
		if(link) {
			SecretEntity secretEntity = SessionHandler.getSecretEntity();
			
			if(secretEntity.getAppBindDetails() != null && !"".equals(secretEntity.getAppBindDetails())) {
				
				String packageName = new JSONObject(secretEntity.getAppBindDetails()).getString("packageName").replaceAll("[\\r\\n]", "");		
				
				packageName = Base64.getUrlEncoder().encodeToString(packageName.getBytes());

				endPointParam = !"".equals(endPointParam) ? "&pn="+packageName : "?pn="+packageName;
			}
		}
		
		return "redirect:"+SecurityConstants.SESSION_END_URI+endPointParam;
	}
	
	/**
	 * @return
	 */
	@GetMapping(value="/common/timeGuide")
	public String timeGuide(ModelMap model) {		
		return "contents/business/timeGuide";
	}
	
	
	/**
	 * 트레이스코드 DB직접 저장
	 * 
	 * @return String
	 */
	@PostMapping(value = "/common/ajax/traceJourneySelf")
	@ResponseBody
	public boolean setTraceJourney(@RequestParam(value = "traceCode", required = true) String traceCode) {
		
		SecretEntity secretEntity = SessionHandler.getSecretEntity();

		TraceJourney journeyEntity = new TraceJourney(Long.valueOf(secretEntity.getWcSeq()), 1, traceCode);
		
		log.debug("request Trace journey param : [{}]", journeyEntity);
		
		traceJourneyService.setTraceJourney(journeyEntity);
		
		return true;
	}
	
	/**
	 * 트레이스코드 Wms와 IVR 동시 전송
	 * 
	 * @return String
	 */
	@PostMapping(value = "/common/ajax/traceJourneyWms")
	@ResponseBody
	public boolean setTraceJourneyWithIvr(
			@RequestParam(value = "traceCode", required = true) String traceCode
			) {

		boolean resultCode = false;

		
		if(SYSTEM_TEST_MODE) {
			log.info("TestMode execute WMS traceJourney and IVR goToEnd. traceCode : {}", traceCode);
			return true;
		}
		
		UserEntity entity = SessionHandler.getSessionEntity();

		try {

			WmsRequestEntity entityParam = new WmsRequestEntity();
			
			entityParam.setSid(entity.getSid());
			entityParam.setWmsAccessDeviceCode(entity.getWmsAccessDeviceCode());
			entityParam.setTraceCode(traceCode);
	    	
	    	log.info("WMS traceJourneyWithIvr TraceCode parameters : {}", entityParam);
	
			WmsResponseEntity result = wmsService.setTraceCode(entityParam);
			
			if(result.getResult() != null && "0".equals(result.getResult())) {
				
				JSONObject messageObj = new JSONObject();
				
				messageObj.put("busiCode", CommonConstants.IVR_TRACE_SEND); //ivr와 협의 설계된 업무구분 코드
				messageObj.put("traceCode", traceCode);
				
				entityParam.setMessage(messageObj.toString());
				
				log.info("WMS traceJourneyWithIvr IVR Send parameters : {}", entityParam);
				
				result = wmsService.goToEnd(entityParam);
				
				if(result.getResult() != null && "0".equals(result.getResult())) {
					resultCode = true;
				}
			}
		
		} catch (WmsException e) {
			log.info("WMS traceJourneyWithIvr Fail USID: {}, message : {}", entity.getSid(), e.getMessage());	
		}

		return resultCode;
	}

	/**
	 * 종료 페이지
	 * @param String
	 * @return String
	 */
	@GetMapping(value = "/common/callEnd")
	public String callEnd(ModelMap model, @RequestParam(value = "pn", required = false, defaultValue = "") String pn) {
		
		if(!"".equals(pn)) {
			
			String packageName = "";
			
			try {
				//파라미터 변조 검사
				packageName = new String(Base64.getUrlDecoder().decode(pn));
				
			}catch(IllegalArgumentException e){
				return "redirect:/auth/accessDenied";
			}
			
			model.addAttribute("packageName", packageName);
		}

		return "contents/common/callEnd";
	}
	
	/**
	 * 중복 브라우저 탭 오류 페이지
	 * 
	 * @return String
	 */
	@GetMapping(value = "/common/duplicateNotAllow")
	public String duplicationNotAllow() {
		
		return "contents/common/duplicateNotAllow";
	}
	
	/**
	 * 호전환 페이지
	 * 
	 * @return String
	 */
	@GetMapping(value = "/common/callTrans")
	public String callTrans() {
		
		return "contents/common/callTrans";
	}
	
	/**
	 * 서비스 점검 중 페이지
	 * 
	 * @return String
	 */
	@GetMapping(value = "/common/underMaintenance")
	public String underMain() {
		return "contents/common/underMaintenance";
	}
	
	/**
	 * IVR PROGRESS CHECK
	 * 
	 * @return String
	 */
	@GetMapping(value = "/common/prgchk")
	public String ivrProgressCheck(ModelMap model) {
		
		UserEntity u_entity = SessionHandler.getSessionEntity();
		
		int progressType = u_entity.getShowArsProgressType();
		String redirectUri = u_entity.getProgressRedirect();
		
		model.addAttribute("showArsProgressType", progressType);
		model.addAttribute("redirectUri", redirectUri);
		
		return "contents/common/progress";
		//return progressType == 0 && !StringUtils.hasLength(redirectUri) ?  "redirect:/main" : "contents/common/progress";
	}
	
	/**
	 * 상담사 근무 시간 확인
	 * 
	 * @return String
	 * @throws ParseException 
	 * @throws Exception 
	 */
	 
	@PostMapping(value = "/common/ajax/checkWorkTime")
	@ResponseBody
	public boolean checkWorkTime(
			@RequestParam(value = "cs_time_st", required = false, defaultValue = "") String cs_time_st,
			@RequestParam(value = "cs_time_ed", required = false, defaultValue = "") String cs_time_ed
			) throws ParseException{
		
		UserEntity userEntity = SessionHandler.getSessionEntity();
		
		String workTime = "090000-180000";
		
		if(StringUtils.hasLength(cs_time_st) && StringUtils.hasLength(cs_time_ed)) {
			workTime = cs_time_st.replace(":", "")+"00-"+cs_time_ed.replace(":", "")+"00";
		}else if(StringUtils.hasLength(userEntity.getWorkTime())){
			workTime = userEntity.getWorkTime();
		}
		
		String[] timeArr = workTime.split("-");					
		
		String startTime = timeArr[0];
		String endTime = timeArr[1];

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HHmmss");
		
		LocalTime str_time = LocalTime.parse(startTime, dateFormat);
		LocalTime end_time = LocalTime.parse(endTime, dateFormat);
		
		LocalTime current_time = LocalTime.now();

		if(current_time.compareTo(str_time) < 0 || end_time.compareTo(current_time) < 0) {
			log.debug("Request Counselor work time check is fail. SID : {}, start time : [{}], end time : [{}], current time : [{}]", userEntity.getSid(), str_time, end_time, current_time);
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * progress 설정 (보이는 self서비스용 프로그레스 셋)
	 * @param request
	 * @return
	 */
	@PostMapping(value="/common/set/progress")
	@ResponseBody
	public boolean progress(@RequestParam(value = "tp", required = true, defaultValue = "1") Integer tp) {
		
		UserEntity entity = SessionHandler.getSessionEntity();
		entity.setShowArsProgressType(tp);
		
		return true;
	}

}
