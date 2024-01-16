package com.deotis.digitalars.mapper.business;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.deotis.digitalars.model.MasterBanner;
import com.deotis.digitalars.model.MasterNotice;
import com.deotis.digitalars.model.Mblob;
import com.deotis.digitalars.model.SiteInfo;
import com.deotis.digitalars.util.collection.DMap;

@Mapper
public interface MainMapper {

	public MasterNotice getNoticeList(@Param("params") DMap<String, Object> params);
	public List<MasterBanner> getBannerList(@Param("params") DMap<String, Object> params);
	public SiteInfo getSiteInfo(@Param("params") DMap<String, Object> params);
	public String getEnterTrace(@Param("params") DMap<String, Object> params);  
	public List<Mblob> getBlobList(@Param("params") DMap<String, Object> params);

}
