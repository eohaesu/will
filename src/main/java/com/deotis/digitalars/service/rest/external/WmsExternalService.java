package com.deotis.digitalars.service.rest.external;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import com.deotis.digitalars.constants.WmsConstants;
import com.deotis.digitalars.model.UserEntity;
import com.deotis.digitalars.model.WmsProperties;
import com.deotis.digitalars.model.WmsRequestEntity;
import com.deotis.digitalars.model.WmsResponseEntity;
import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.system.exception.WmsException;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.system.rest.client.DeotisTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description Base external WMS rest communication service
 */

@Slf4j
@Service
public class WmsExternalService {
	
	private final DeotisTemplate deotisTemplate;
	private final WmsProperties wmsProperties;

	public WmsExternalService(DeotisTemplate deotisTemplate, WmsProperties wmsProperties) {
		this.deotisTemplate = deotisTemplate;
		this.wmsProperties = wmsProperties;
	}
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;

	/**
	 * get usid from crid (crid로부터 usid)
	 * @return WmsResponseEntity
	 * @param String crid
	 * @throws WmsException 
	 */
	public WmsResponseEntity getUsidFromCrid(String crid, boolean isReTry) throws WmsException{

		WmsResponseEntity result = null;
		
		String[] server = isReTry ? wmsProperties.getServer().get(getRecusiveDevice()) : wmsProperties.getServer().get("balancer");
		
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.CRID_QUERY);
		uriBuilder.port(server[1]);
		
		log.info("request SID from crid:{}, sessionId:{}", crid, RequestContextHolder.getRequestAttributes().getSessionId());
		
		try {
			
			JSONObject param = new JSONObject();
	
			param.put("Crid", crid);

			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
							uriBuilder.build().toUriString(),
							HttpMethod.POST,
							httpEntity,
							new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			result = response.getBody();
			
		}catch(ResourceAccessException e){
			if(isReTry) {
				log.error("####################The WMS engine has been shutdown.########################");
				setSessionEnd();
			}else {
				result = getUsidFromCrid(crid, true);			
			}
			
		}catch(RuntimeException e){
			throw new WmsException(crid);
		}

		return result;
	}
	
	/**
	 * WMS wasStart (가동 시작)
	 * @return WmsResponseEntity
	 * @param String crid, String deviceCode
	 * @throws WmsException 
	 */
	public WmsResponseEntity setWasStart(WmsRequestEntity requestEntity) throws WmsException {
		
		WmsResponseEntity result = null;

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.WAS_START);
		uriBuilder.port(server[1]);
		
		log.info("request WasStart URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("launcherName", requestEntity.getLauncherName());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			result = response.getBody();
			
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());
		}	

		return result;
	}
	
	/**
	 * Alive Check (WMS 가동 상태 확인)
	 * @return WmsResponseEntity
	 * @param String usid
	 * @throws WmsException 
	 */
	public WmsResponseEntity getAliveCheck(String usid, String deviceCode) throws WmsException {
		
		ResponseEntity<WmsResponseEntity> response = null;
		
		String[] server = wmsProperties.getServer().get(deviceCode);
		
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.ALIVE_CHECK);
		uriBuilder.port(server[1]);
		
		log.info("request WasStart from USID:{},  sessionId:{}", usid, RequestContextHolder.getRequestAttributes().getSessionId());
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", usid);
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);
			
			log.debug(uriBuilder.build().toUriString());

			response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
		}catch(RuntimeException e){
			throw new WmsException(usid);
		}	
		
		log.debug("response:{}", response);

		return response.getBody();
	}
	
	/**
	 * Call End (통화 종료)
	 * @return WmsResponseEntity
	 * @param String usid
	 * @throws WmsException 
	 */
	public WmsResponseEntity setCallEnd(WmsRequestEntity requestEntity) throws WmsException{
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}

		WmsResponseEntity result = null;
		
		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.UNBIND);
		uriBuilder.port(server[1]);
		
		log.info("request CallEnd URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());

			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});

			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = setCallEnd(requestEntity);
			}else {
				result = response.getBody();
			}
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[setCallEnd]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = setCallEnd(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}	
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), e.getMessage());	
		}	

		
		return result;
	}
	
	/**
	 * TraceCode (여정정보 기록)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity setTraceCode(WmsRequestEntity requestEntity) throws WmsException{
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}

		WmsResponseEntity result = null;
		
		log.debug("setTraceCode WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());
		
		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.TRACECODE);
		uriBuilder.port(server[1]);
		
		log.info("request TraceCode URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("TRACE_CODE", requestEntity.getTraceCode());

			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});

			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = setTraceCode(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[setTraceCode]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = setTraceCode(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}	
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), e.getMessage());	
		}	

		
		return result;
	}
	
	/**
	 * Play Start (음성 플레이)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity playStart(WmsRequestEntity requestEntity) throws WmsException{
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("playStart WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.PLAY_START);
		uriBuilder.port(server[1]);

		log.info("request PlayStart URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("TocData", requestEntity.getTocData());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);
	
			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = playStart(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[playStart]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = playStart(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());
		}	
			
		return result;
	}
	
	/**
	 * Play Stop (음성 플레이 중지)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity playStop(WmsRequestEntity requestEntity) throws WmsException {
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("playStop WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.PLAY_STOP);
		uriBuilder.port(server[1]);

		log.info("request PlayStop URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = playStop(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[playStop]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = playStop(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());
		}	
		
		return result;
	}
	
	/**
	 * CTI Wait Time (상담사 대기시간)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity getCtiWaitTime(WmsRequestEntity requestEntity) throws WmsException {
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("getCtiWaitTime WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.CTI_WAIT);
		uriBuilder.port(server[1]);
		
		log.info("request CTI Wait Time URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("QDN", requestEntity.getQdn());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("TimeoutMs", requestEntity.getTimeoutMs());
			
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = getCtiWaitTime(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[getCtiWaitTime]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = getCtiWaitTime(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * Call Transfer(호전환)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity setCallTranfer(WmsRequestEntity requestEntity) throws WmsException {
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("setCallTranfer WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.CALL_TRANFER);
		uriBuilder.port(server[1]);

		log.info("request Call Transfer URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("VDN", requestEntity.getVdn());
			param.put("QDN", requestEntity.getQdn());
			param.put("UUI", requestEntity.getUui());
			param.put("TimeoutMs", requestEntity.getTimeoutMs());
			param.put("Trid", requestEntity.getTrid());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = setCallTranfer(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[setCallTranfer]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = setCallTranfer(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * IVR 메세지 전송
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity goToEnd(WmsRequestEntity requestEntity) throws WmsException {

		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}

		WmsResponseEntity result = null;
		
		log.debug("goToEnd WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.GO_TO_END);
		uriBuilder.port(server[1]);

		log.info("request Go To End URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			String message = requestEntity.getMessage();
			
			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("Message", isValidJson(message) ? message.replaceAll("\"", "'") : message);
			//param.put("Message", isValidJson(message) ? new JSONObject(message) : message);

			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = goToEnd(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS Connection ResourceAccessException[goToEnd]");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = goToEnd(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * IVR userData save
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity userDataSave(WmsRequestEntity requestEntity) throws WmsException {
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("userDataSave WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.USER_DATA_SAVE);
		uriBuilder.port(server[1]);

		log.info("request userDataSave URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			SecretEntity entity = SessionHandler.getSecretEntity();
			
			param.put("Ani", entity.getAni());
			param.put("USID", requestEntity.getSid());
			param.put("UserData", requestEntity.getUserData());
			param.put("AppBindDetails", requestEntity.getAppBindDetails());
			param.put("TimeoutSec", requestEntity.getTimeoutSec());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = userDataSave(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS ResourceAccessException[userDataSave]. Change to the Balancer and recursive call.");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = userDataSave(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * SendVariable(IVR)
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity sendVariable(WmsRequestEntity requestEntity) throws WmsException {
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("sendVariable WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.SEND_VARIABLE);
		uriBuilder.port(server[1]);

		log.info("request sendVariable URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();
			
			String message = requestEntity.getMessage();

			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
			param.put("Type", requestEntity.getType());
			param.put("Message", message);
			//param.put("Message", isValidJson(message) ? new JSONObject(message) : message);
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = sendVariable(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS ResourceAccessException[sendVariable]. Change to the Balancer and recursive call.");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = sendVariable(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * IVR check Web Live status
	 * @return WmsResponseEntity
	 * @param WmsRequestEntity requestEntity
	 * @throws WmsException 
	 */
	public WmsResponseEntity webLive(WmsRequestEntity requestEntity) throws WmsException {
		
		if(SYSTEM_TEST_MODE) {
			return getTestWmsResult(requestEntity);
		}
		
		WmsResponseEntity result = null;
		
		log.debug("webLive WMS DEVICE CODE  ::::::{}", requestEntity.getWmsAccessDeviceCode());

		String[] server = wmsProperties.getServer().get(requestEntity.getWmsAccessDeviceCode());

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(server[0]+WmsConstants.WEB_LIVE);
		uriBuilder.port(server[1]);

		log.info("request webLive URI:[{}], requestEntity params [{}]", uriBuilder.build().toUriString(), requestEntity);
		
		try {
		
			JSONObject param = new JSONObject();

			param.put("USID", requestEntity.getSid());
			param.put("WmsAccessDeviceCode", requestEntity.getWmsAccessDeviceCode());
	
			HttpHeaders headers = new HttpHeaders();
			
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<?> httpEntity = new HttpEntity<>(param.toString(), headers);

			ResponseEntity<WmsResponseEntity> response = deotisTemplate.exchange(
						uriBuilder.build().toUriString(),
						HttpMethod.POST,
						httpEntity,
						new ParameterizedTypeReference<WmsResponseEntity>() {});
			
			//Check WMSAccessDeviceCode has been changed
			if(checkChangedDevice(response.getBody(), requestEntity)) {
				//Recursive call if changed
				result = webLive(requestEntity);
			}else {
				result = response.getBody();
				checkDeviceCode(result);
			}
		}catch(ResourceAccessException e){
			log.debug("WMS ResourceAccessException[webLive]. Change to the Balancer and recursive call.");
			if(checkConnectionFailCount()) {
				log.debug("Change to the Balancer and recursive call.");
				requestEntity.setWmsAccessDeviceCode(getRecusiveDevice());
				result = webLive(requestEntity);
			}else {
				log.debug("Connection fail count is max. Set session end");
				throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
			}
		}catch(RuntimeException e){
			throw new WmsException(requestEntity.getSid(), requestEntity, e.getMessage());	
		}	
			
		return result;
	}
	
	/**
	 * Check for deviceCode changed
	 * @return boolean
	 * @param WmsResponseEntity response, WmsRequestEntity requestEntity
	 */
	private boolean checkChangedDevice(WmsResponseEntity response, WmsRequestEntity requestEntity){
		
		//WMSAccessDeviceCode has been changed
		if("2".equals(response.getResult()) && "9510".equals(response.getMessage())) {
			
			String changedDeviceCode = response.getWmsAccessDeviceCode();
			log.info("######## checkChangedDevice ######## new DeviceCodeNumber is {}", changedDeviceCode);
			setSessionDeviceChange(changedDeviceCode);
			
			requestEntity.setWmsAccessDeviceCode(changedDeviceCode);

			return true;

		}else {
			return false;
		}
	}

	/**
	 * Set deviceCode changed to session model
	 * @return void
	 * @param String deviceCode
	 */
	private void setSessionDeviceChange(String deviceCode) {

		UserEntity entity = SessionHandler.getSessionEntity();
		SecretEntity secretEntity = SessionHandler.getSecretEntity();

		log.debug(" ######## setSessionDeviceChange ######## sid:{}", entity.getSid());

		entity.setWmsAccessDeviceCode(deviceCode);
		secretEntity.setWmsAccessDeviceCode(deviceCode);
		
		SessionHandler.setSecretEntity(secretEntity);
	}
	
	/**
	 * Check DeviceCode has been changed
	 * @return void
	 * @param WmsResponseEntity
	 */
	private void checkDeviceCode(WmsResponseEntity response){
		
		SecretEntity secretEntity = SessionHandler.getSecretEntity();
		//장비 번호를 -1을 받을 경우는 call end
		if("-1".equals(response.getWmsAccessDeviceCode())) {
			log.info(" ######## response WMSdeviceCode recieve -1. Just set session End ########");
			setSessionEnd();
		}else {
			if(!secretEntity.getWmsAccessDeviceCode().equals(response.getWmsAccessDeviceCode())) {
				log.info("######## DeviceChange before:{}, after:{}", secretEntity.getWmsAccessDeviceCode(), response.getWmsAccessDeviceCode());
				setSessionDeviceChange(response.getWmsAccessDeviceCode());
			}
			
			SessionHandler.getSessionEntity().setWmsConnectFailCount(0); // 정상 연결인 경우 실패 카운트 초기화
		}
	}
	
	/**
	 * Set WMS connection fail count
	 * @return boolean
	 * @throws InterruptedException 
	 */
	private boolean checkConnectionFailCount(){
		UserEntity entity = SessionHandler.getSessionEntity();
		
		if(entity.getWmsConnectFailCount() < 2) {
			
			try {
				log.info(" ######## WMS Connection is fail. Just WmsConnectFailCount+1 ########");
				Thread.sleep(1500); //IVR 재 커넥션 시간
				entity.setWmsConnectFailCount(entity.getWmsConnectFailCount()+1);
			} catch (InterruptedException e) {
				log.error(" ######## InterruptedException {} ########", e.getMessage());
				return true;
			}
			
			return true;
			
		}else {
			log.debug(" ######## WMS Connection fail count is full. Just set session End ########");
			setSessionEnd();
			return false;
		}
	}

	/**
	 * Set Session End by WMS Connection
	 * @return void
	 */
	private void setSessionEnd() {
		
		if(!SYSTEM_TEST_MODE) {
			UserEntity entity = SessionHandler.getSessionEntity();
			
			entity.setRecieve_callend(true);
		}
	}
	
	
	private WmsResponseEntity getTestWmsResult(WmsRequestEntity requestEntity) {
		
		WmsResponseEntity result = new WmsResponseEntity();
		
		result.setResult("0");
		result.setUsid(requestEntity.getSid());
		result.setMessage("test message");
		result.setWmsAccessDeviceCode(requestEntity.getWmsAccessDeviceCode());
		result.setDetailMsg(null);

		
		return result;
	}
	
	private boolean isValidJson(String jsonVal) {
		
		try {
			new JSONObject(jsonVal);
		}catch(JSONException e) {
			return false;
		}
		return true;
	}
	
	private String getRecusiveDevice() {
		
		return "1".equals(SessionHandler.getSessionEntity().getWmsAccessDeviceCode()) ? "2" : "1";
	}
}