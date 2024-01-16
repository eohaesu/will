package com.deotis.digitalars.service.business;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import com.deotis.digitalars.mapper.business.MainMapper;
import com.deotis.digitalars.model.MasterBanner;
import com.deotis.digitalars.model.MasterNotice;
import com.deotis.digitalars.model.Mblob;
import com.deotis.digitalars.model.SiteInfo;
import com.deotis.digitalars.util.collection.DMap;

/**
 * 
 * @author jongjin
 * @description service for main
 */
@Service
public class MainService {

	private final SqlSessionTemplate sqlSession;
	
	public MainService(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

	
	/**
	 * 사이트 정보 조회
	 * @param DMap<String, Object>
	 * @return SiteInfo
	 */
	public SiteInfo getSiteInfo(DMap<String, Object> params) {

		MainMapper mapper = sqlSession.getMapper(MainMapper.class);

		return mapper.getSiteInfo(params);
	}
	
	/**
	 * 알림 조회
	 * @param String
	 * @return List<MasterMenu>
	 */
	public MasterNotice getNotice(DMap<String, Object> params) {

		MainMapper mapper = sqlSession.getMapper(MainMapper.class);
		
		MasterNotice result = mapper.getNoticeList(params);
		
		return result;
	}

	/**
	 * 배너 조회
	 * @param String
	 * @return List<MasterBanner>
	 */
	public List<MasterBanner> getMainBanner(DMap<String, Object> params) {

		MainMapper mapper = sqlSession.getMapper(MainMapper.class);
		List<MasterBanner> resultList = mapper.getBannerList(params);
		return resultList;
	}
	
	/**
	 * 인입 개인화 트레이스 코드 조회
	 * @param DMap<String, Object>
	 * @return String
	 */
	public String getEnterTrace(DMap<String, Object> params) {

		MainMapper mapper = sqlSession.getMapper(MainMapper.class);
		
		return  mapper.getEnterTrace(params);
	}

	/**
	 * blob_data 가져오기
	 * 
	 * @return
	 */
	public List<Mblob> getBlobList(DMap<String, Object> params) {

		MainMapper mapper = sqlSession.getMapper(MainMapper.class);
		
		List<Mblob> resultList = mapper.getBlobList(params);

		return resultList;
	}

}