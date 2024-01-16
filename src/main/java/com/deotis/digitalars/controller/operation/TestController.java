package com.deotis.digitalars.controller.operation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.deotis.digitalars.security.model.SecretEntity;
import com.deotis.digitalars.service.common.RedisTemplateService;
import com.deotis.digitalars.service.rest.external.ExternalSampleService;
import com.deotis.digitalars.system.handler.SessionHandler;
import com.deotis.digitalars.util.collection.DMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/test")
@Controller
public class TestController {
	
	@Value("${digitalars.default.site}")
	private String DEFAULT_SITE_CODE;
	
	@Value("${system.test.mode}")
	private boolean SYSTEM_TEST_MODE;
	
	@Value("${system.test.ivr}")
	private boolean SYSTEM_TEST_IVR;

	@Value("${jasypt.encryptor.password}")
    private String JASYPT_KEY;

	private final ExternalSampleService externalSampleService;
	
	private final RedisTemplateService redisTemplateService;

	/*
	@Autowired
	private StringRedisTemplate redisTemplate;
	*/
	
	/**
	 * Local Test
	 * @param model
	 * @param session
	 * @param request
	 * @param response
	 * @return String
	 */
	@GetMapping(value = "/localTestPage")
	public String localTestPage(ModelMap model) {
		
		if(SYSTEM_TEST_MODE) {
			log.debug("Enter local test page");
			
			model.addAttribute("DEFAULT_SITE_CODE", DEFAULT_SITE_CODE);

			return "contents/test/authorizationMock";
		}else {
			return "redirect:/auth/accessDenied";
		}

	}
	
	/**
	 * RedisTemplate test
	 * @param model
	 */
	@PostMapping(value = "/redisGn")
	@ResponseBody
	public void redisTest() {
		if(SYSTEM_TEST_MODE) {

			for(int i= 0; i<100; i++) {
				String key = "dars:willvi:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:"+UUID.randomUUID();
				redisTemplateService.addKeyOptValue(key, "", 60);
			}
			//int deleteCnt = redisTemplateService.cleanKeysWithScan();
		}
	}
	
	/**
	 * RedisTemplate test
	 * @param model
	 */
	@PostMapping(value = "/redisClean")
	@ResponseBody
	public Integer redisClean() {
		
		int deleteCount = 0;
		
		if(SYSTEM_TEST_MODE) {

			deleteCount = redisTemplateService.cleanKeysWithScan();

		}
		return deleteCount;
	}

	
	/**
	 * IVR CONTROL REST TEST
	 * @param model
	 */
	@GetMapping(value = "/ivrControlMock")
	public String ivrControlMock(ModelMap model, HttpSession session) {
		if(SYSTEM_TEST_MODE) {

			return "contents/test/ivrControlMock";
		}else {
			return "redirect:/auth/accessDenied";
		}

	}
	
	/**
	 * ivrTestMock
	 * @param model
	 */
	@GetMapping(value = "/ivrTestMock")
	public String ivrTestMock(ModelMap model, HttpSession session) {
		if(SYSTEM_TEST_IVR && session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
			SecretEntity entity = SessionHandler.getSecretEntity();
			
			model.addAttribute("userData", entity.getUserData());
			model.addAttribute("appBindDetails", entity.getAppBindDetails());
			
			return "contents/test/ivrTestMock";
		}else {
			return "redirect:/auth/accessDenied";
		}

	}

	/**
	 * application enc encode/decode generate
	 * @param model
	 * @param request
	 * @return String
	 */
	@GetMapping(value = "/encGenerate")
	public void encGenerate(HttpServletResponse response, 
			@RequestParam(value = "type", required = true, defaultValue = "enc") String type,
			@RequestParam(value = "key", required = true, defaultValue = "") String key
			) {
		
		response.setContentType("text/html");

		PrintWriter out = null;
		
		if(SYSTEM_TEST_MODE) {
			try {

		        String result = "enc".equals(type) ? jasyptEncrypt(key) : jasyptDecryt(key);
		        
				out = response.getWriter();
	
				out.println("<html><body>");
	
				out.println("<h2>" + result + "</h2>");
				out.println("<hr>");
				out.println("TEST Time on the server is: " + new java.util.Date());
	
				out.println("</body></html>");

			} catch (IOException e) {
				log.error("Fail to EncGenerateTest. message:{}", e.getMessage());
			} finally {
				if(out != null){
					out.flush();
				}
			}
		}
	}
	
	private String jasyptEncrypt(String input) {
       
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(JASYPT_KEY);
        return encryptor.encrypt(input);
    }

    private String jasyptDecryt(String input){

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(JASYPT_KEY);
        return encryptor.decrypt(input);
    }
	
	
	/**
	 * RestTemplate test
	 * 
	 * @return String
	 */
	@PostMapping(value = "/ajax/testRestTemplate")
	@ResponseBody
	public String testRestTemplate() {
		
		if(SYSTEM_TEST_MODE) {
			Map<String, Object> result = externalSampleService.getRestTemplateSample();

			return "{ \"result\" : \""+result+"\" }";
		}else {
			return null;
		}
	}
	
	/**
	 * ReactiveClient test
	 * 
	 * @return String
	 */
	@PostMapping(value = "/ajax/testReactiveClient")
	@ResponseBody
	public String testReactiveClient() {
		
		if(SYSTEM_TEST_MODE) {
			DMap<String, Object> result = externalSampleService.getReactiveClientSample();

			return "{ \"result\" : \""+result+"\" }";
		}else {
			return null;
		}
	}
	
	/**
	 * SSE Emitter longPolling test
	 * 
	 * @return ResponseEntity<SseEmitter>
	 */
	@PostMapping(value="/sse/emitter", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> handleRequest () {

        final SseEmitter emitter = new SseEmitter((long)(1000*60*5));
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            for (int i = 0; i < 1000; i++) {
                try {
                	log.debug("SEND:::::::"+i);
                    emitter.send(i + " - ", MediaType.TEXT_PLAIN);

                    Thread.sleep(500);
                    
                } catch (IOException | NullPointerException | InterruptedException e) {
                    emitter.completeWithError(e);
                    return;
                }
            }
            emitter.complete();
        });
        return new ResponseEntity<SseEmitter>(emitter, HttpStatus.OK);
    }
	
	/**
	 * DeffredResult longPolling test
	 * 
	 * @return DeferredResult<ResponseEntity<?>>
	 */
	@GetMapping("/deffredTest")
	public DeferredResult<ResponseEntity<?>> handleReqDefResult(HttpServletRequest request, @RequestParam(value = "btoken", required = false) String btoken) {
	    log.info("Received async-deferredresult request");
	    DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
	    
	    ForkJoinPool.commonPool().submit(() -> {
	    	log.info("Processing in separate thread");
	        try {
	        	TimeUnit.MILLISECONDS.sleep(500);
	        } catch (InterruptedException e) {
	        	log.info("exception", e.getMessage());
	        }
	        output.setResult(ResponseEntity.ok("ok"));
	    });
	    
	    log.info("servlet thread freed");
	    return output;
	}

	@PostMapping("/post403Test")
	@ResponseBody
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public DMap<String, Object> axiosPostTest(Map<String, Object> params) {
		DMap<String, Object> resultMap = new DMap<String,Object>();
		resultMap.put("test", "test");
		resultMap.put("params", params);

		return resultMap;
	}

}