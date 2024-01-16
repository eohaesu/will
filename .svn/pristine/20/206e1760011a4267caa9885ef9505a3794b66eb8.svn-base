package com.deotis.digitalars.system.filter;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author jongjin
 * @description get ContextRefreshedEvent application bean properties on log
 */

@Slf4j
@Component
public class SystemPropertiesListener {
	
	@Value("${system.log.show.properties}")
	private boolean SHOW_PROPERTIES;

    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
    	
    	if(SHOW_PROPERTIES) {
    		
    		ConfigurableEnvironment env = (ConfigurableEnvironment) event.getApplicationContext().getEnvironment();
    		
        	env.getPropertySources()
            .stream()
            .filter(ps -> ps instanceof MapPropertySource)
            .map(ps -> ((MapPropertySource) ps).getSource().keySet())
            .flatMap(Collection::stream)
            .distinct()
            .sorted()
            .forEach(key -> log.info("{}={}", key, env.getProperty(key)));
    	}
    }
}