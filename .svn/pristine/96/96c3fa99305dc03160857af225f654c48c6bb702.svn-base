package com.deotis.digitalars.service.rest.internal;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import com.deotis.digitalars.mapper.rest.TraceJourneyMapper;
import com.deotis.digitalars.model.TraceJourney;

/**
 * 
 * @author jongjin
 * @description service for internal rest trace journey
 */
@Service
public class TraceJourneyService {

	private final SqlSessionTemplate sqlSession;
	
	public TraceJourneyService(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

	/**
	 * 외부 trace 통계 기록
	 * @param TraceJourneyEntity
	 * @return void
	 */
	public void setTraceJourney(TraceJourney entity) {
		TraceJourneyMapper mapper = sqlSession.getMapper(TraceJourneyMapper.class);
		mapper.setTraceJourney(entity);
	}
}