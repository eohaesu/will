package com.deotis.digitalars.security.model;

import java.io.Serializable;

import org.springframework.security.core.SpringSecurityCoreVersion;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author jongjin
 * @description security call entity from wasstart of wms
 */

@Getter
@Setter
@ToString
public class SecretEntity implements Serializable{
	
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private String siteCode;
	private String dnis;
	private String ani;
	private String wmsAccessDeviceCode;
	private String userData;
	private String launcherName;
	private String appBindDetails;
	private String wcSeq;
	private String btoken;
	
	@Builder
	public SecretEntity(
			String siteCode,
			String dnis,
			String ani,
			String wmsAccessDeviceCode,
			String userData,
			String launcherName,
			String appBindDetails,
			String wcSeq,
			String btoken) {	
		this.siteCode = siteCode;
		this.dnis = dnis;
		this.ani = ani;
		this.wmsAccessDeviceCode = wmsAccessDeviceCode;
		this.userData = userData;
		this.launcherName = launcherName;
		this.appBindDetails = appBindDetails;
		this.wcSeq = wcSeq;
		this.btoken = btoken;
	}
	
}
