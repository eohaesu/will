package com.deotis.digitalars.system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.deotis.digitalars.service.common.RedisTemplateService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description cron scheduler
 * 
**/
@Slf4j
@Configuration
@EnableScheduling
public class SidCleanScheduleConfig  {
	
	@Autowired
	private RedisTemplateService redisTemplateService;

	@Scheduled(cron = "${system.scheduled.crondata}")
	public void start() {
		int result = redisTemplateService.cleanKeysWithScan();
		log.info("Repository clean job excute result count : {}", result);
	}
}