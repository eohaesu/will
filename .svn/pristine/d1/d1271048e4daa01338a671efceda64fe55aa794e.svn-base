package com.deotis.digitalars.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 
 * @author jongjin
 * @description base session user entity
 */

@Getter
@Setter
@ToString
public class UserEntity implements Serializable{

	private static final long serialVersionUID = 8333114936264245561L;
	private String sid;
	private String siteCode;
	private String wmsAccessDeviceCode;
	private boolean recieve_callend;
	private String wmsEventName;
	private String counselorWaitTime;
	private String counselorWaitCount;
	private Object ivrControlInfo;
	private String customerName;
	private String memberType;
	private String workTime;
	private String menuDiv;
	private String cmenuDiv;
	private String timeStatus; //1:주간, 2:야간, 3:심야공휴일주말
	private String ctiConnId; 

	private Integer wmsConnectFailCount;
	private Integer showArsProgressType; //ivr전문처리 대기용 progressPage 처리
	private String progressRedirect; //goToEnd 대기 후 redirection 페이지 정보
	private SiteInfo siteInfo;
	
	@Builder
	public UserEntity(String sid, 
			String siteCode, 
			String wmsAccessDeviceCode, 
			String customerName, 
			String memberType, 
			String menuDiv, 
			String cmenuDiv, 
			String timeStatus,
			String ctiConnId
			) {
		this.sid = sid;	
		this.siteCode = siteCode;	
		this.wmsAccessDeviceCode = wmsAccessDeviceCode;
		this.customerName = customerName;
		this.memberType = memberType;
		this.menuDiv = menuDiv;
		this.cmenuDiv = cmenuDiv;
		this.timeStatus = timeStatus;
		this.ctiConnId = ctiConnId;
		this.recieve_callend = false;
		this.showArsProgressType = 0;
		this.wmsConnectFailCount = 0;
		
		if("1".equals(timeStatus)) {//주간
			this.workTime = "090000-180000";
		}else if("2".equals(timeStatus)) {//야간
			this.workTime = "180000-190000";
		}else {//심야
			this.workTime = "190000-090000";
		}
	}
	
}
