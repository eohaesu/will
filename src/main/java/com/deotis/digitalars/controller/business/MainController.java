package com.deotis.digitalars.controller.business;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.deotis.digitalars.model.MasterBanner;
import com.deotis.digitalars.model.MasterCustomMenuData;
import com.deotis.digitalars.model.MasterMenu;
import com.deotis.digitalars.model.MasterMenuData;
import com.deotis.digitalars.model.MasterNotice;
import com.deotis.digitalars.model.Mblob;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.business.MainService;
import com.deotis.digitalars.service.common.MenuService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.util.collection.DMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

	//private final MessageSource messageSource;
	private final MainService mainService;
	private final MenuService menuService;
	
	@Value("${system.test.ivr}")
	private boolean SYSTEM_TEST_IVR;


	/**
	 * main 페이지
	 * @param model
	 * @param session
	 * @param request
	 * @param response
	 * @return String
	 */
	@GetMapping(value = "/main")
	public String arsMain(ModelMap model) {

		SecretEntity secret = SessionHandler.getSecretEntity();
		UserEntity entity = SessionHandler.getSessionEntity();

		if(secret.getAppBindDetails() != null && !"".equals(secret.getAppBindDetails())) {
			model.addAttribute("packageName", new JSONObject(secret.getAppBindDetails()).getString("packageName"));
		}

		log.trace("Attempt to MainPage IVR entity : [{}]", secret);
		
		model.addAttribute("wcSeq", secret.getWcSeq());
		model.addAttribute("ivrTest", SYSTEM_TEST_IVR);
		model.addAttribute("customerName", entity.getCustomerName());
		model.addAttribute("btoken", secret.getBtoken());
		model.addAttribute("timeStatus", entity.getTimeStatus());
		
		return "contents/business/main";
	}

	/**
	 * 배너 목록 조회
	 * 
	 * @return String
	 * @throws JsonProcessingException 
	 */
	 
	@PostMapping(value = "/main/ajax/getMainBanner")
	@ResponseBody
	public Optional<String> getMainBanner(){

		SecretEntity secret = SessionHandler.getSecretEntity();
		
		DMap<String, Object> params = new DMap<>();
		
		params.put("site_code", secret.getSiteCode());
		//menu_code를 dnis 로 비교
		//UserEntity에 menu_code가 없으므로 TEST 용으로 compDiv 사용
		//차후에 ivr에서 menu_code 받기로함
		params.put("dnis", secret.getDnis());

		List<MasterBanner> bannerList = mainService.getMainBanner(params);

		try {
			return (bannerList != null && bannerList.size() > 0) ? Optional.of(new ObjectMapper().writeValueAsString(bannerList)) : Optional.empty();
		} catch (JsonProcessingException e) {
			log.error("#getMainBanner JsonProcessingException : {}", e.toString());
			return Optional.empty();
		}

	}
	
	/**
	 * 공지사항 조회
	 * 
	 * @return String
	 */
	@PostMapping(value = "/main/ajax/getNotice")
	@ResponseBody
	public MasterNotice getNotice(ModelMap map, HttpSession session, HttpServletRequest request){
		
		SecretEntity secret = SessionHandler.getSecretEntity();
		
		DMap<String, Object> params = new DMap<>();
		
		params.put("site_code", secret.getSiteCode());
		params.put("dnis", secret.getDnis());
		
		MasterNotice noticeList = mainService.getNotice(params);

		return noticeList;
	}
	
	/**
	 * 메뉴 데이터 조회
	 * 
	 * @return String
	 * @throws JsonProcessingException 
	 */
	 
	@PostMapping(value = "/common/ajax/getArsMenuDataTree")
	@ResponseBody
	public String getMainMenu(){

		DMap<String, Object> params = new DMap<String, Object>();
		String result = null;
		
		UserEntity userEntity = SessionHandler.getSessionEntity();

		params.put("site_code", userEntity.getSiteCode());
		
		//ivr로부터 메뉴코드를 받는 경우 확인
		boolean ivrGiveMenuCode = StringUtils.hasLength(userEntity.getMenuDiv()) ? true : false;

		if(ivrGiveMenuCode) {
			params.put("menu_code", userEntity.getMenuDiv());
		}

		MasterMenu master_menu = menuService.getArsMenu(params);
		
		if(master_menu != null && master_menu.getMenu_code() > 0) {
			
			if(!ivrGiveMenuCode) {
				userEntity.setMenuDiv(master_menu.getMenu_code().toString());
				params.put("menu_code", master_menu.getMenu_code());
			}

			List<MasterMenuData> master_menu_data = menuService.getArsMenuDataTree(params);
			
			log.trace("SID : [{}] Menu Data entity : [{}]", userEntity.getSid(), master_menu_data);

			if(master_menu_data != null && master_menu_data.size() > 0) {
				try {
					result = new ObjectMapper().writeValueAsString(master_menu_data);
				} catch (JsonProcessingException e) {
					log.error("#getMainMenuTree JsonProcessingException : {}", e.toString());
				}
			}
		}

		return result;
		
	}

	
	/**
	 * 추천메뉴 & 즐겨찾기 조회
	 * @author Byeongok
	 * @return String
	 * @throws JsonProcessingException 
	 */
	@PostMapping(value = "/main/ajax/getSuggestMenu")
	@ResponseBody
	public Optional<String> getSuggestMenu(){
		
		DMap<String, Object> params = new DMap<>();
		
		List<MasterCustomMenuData> suggestMenuList = null;
		
		UserEntity userEntity = SessionHandler.getSessionEntity();

		params.put("site_code", userEntity.getSiteCode());
		
		//ivr로부터 메뉴코드를 받는 경우 확인
		boolean ivrGiveCMenuCode = StringUtils.hasLength(userEntity.getCmenuDiv()) ? true : false;

		if(ivrGiveCMenuCode) {
			params.put("menu_code", userEntity.getCmenuDiv());
		}

		MasterMenu master_menu = menuService.getSuggestMenu(params);
		
		if(master_menu != null && master_menu.getMenu_code() > 0) {
			
			if(!ivrGiveCMenuCode) {
				userEntity.setCmenuDiv(master_menu.getMenu_code().toString());
				params.put("menu_code", master_menu.getMenu_code());
			}
			
			suggestMenuList = menuService.getSuggestMenuTree(params);
		}
		
		try {
			return (suggestMenuList != null && suggestMenuList.size() > 0) ? Optional.of(new ObjectMapper().writeValueAsString(suggestMenuList)) : Optional.empty();
		} catch (JsonProcessingException e) {
			log.error("#getSuggestMenuTree JsonProcessingException : {}", e.toString());
			return Optional.empty();
		}

	}

	@PostMapping(value = "/main/ajax/getBlobList")
	@ResponseBody
	public List<Mblob> getBlobList() {
		UserEntity userEntity = SessionHandler.getSessionEntity();
		
		DMap<String, Object> params = new DMap<>();
		params.put("site_code", userEntity.getSiteCode());
		params.put("menu_code", userEntity.getMenuDiv());
		params.put("c_menu_code", userEntity.getCmenuDiv());
		return mainService.getBlobList(params);
	}
}
