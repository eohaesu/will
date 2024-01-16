package com.deotis.digitalars.controller.business;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.deotis.digitalars.constants.IVRConstants;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RequestMapping(value = "/counselor")
@Controller
public class CounselorController{
	
	/**
	 * 상담사 대기 안내
	 * @author ByeongOk
	 * @return
	 */
	@GetMapping(value="/connect/confirm")
	public String counslerConnect(ModelMap model) {
		
		model.addAttribute("separator", IVRConstants.SEPARATOR);
		model.addAttribute("waitCode", IVRConstants.GOTOEND_WAITCOUNT);
		
		return "contents/business/counselorConnectConfirm";
	}
	
	
	/**
	 * 상담사 연결
	 * @author ByeongOk
	 * @return
	 */
	@GetMapping(value="/connect/continue")
	public String counslerConnectDelay() {
		return "contents/business/counselorConnect";
	}
	
	/**
	 * ARS SELF 연결 페이지
	 * 
	 * @return String
	 */
	@GetMapping(value = "/connect/arsSelf")
	public String arsSelf(ModelMap model) {
		return "contents/business/arsSelf";
	}


}