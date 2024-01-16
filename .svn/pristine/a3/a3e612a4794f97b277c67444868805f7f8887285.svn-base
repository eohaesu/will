package com.deotis.digitalars.controller.rest.internal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deotis.digitalars.model.TraceJourney;
import com.deotis.digitalars.service.rest.internal.TraceJourneyService;
import com.deotis.digitalars.system.rest.entity.ResponseEntityWrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/internal/trace")
public class TraceController {

	private final TraceJourneyService traceJourneyService;
	
	/**
	 * 외부 여정 기록 처리
	 * @param wcseq
	 * @param input_type
	 * @param tracecode
	 */
	@PostMapping(value = "/journey")
	public ResponseEntityWrapper<TraceJourney> traceJourney(
			@RequestParam(value = "wcseq", required = true) Integer wcseq,
			@RequestParam(value = "input_type", required = true) Integer input_type,
			@RequestParam(value = "tracecode", required = true) String tracecode){
		
		TraceJourney entity = new TraceJourney(wcseq, input_type, tracecode);
		
		log.debug("request Trace journey param : [{}]", entity);
		
		traceJourneyService.setTraceJourney(entity);

		return new ResponseEntityWrapper<TraceJourney>(entity);		
	}	
}
