package com.shorturl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.shorturl.infra.ErrorCodes;
import com.shorturl.repository.ShortUrlRepository;
import com.shorturl.services.ShortUrlService;
import com.shorturl.util.Base62;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShorturlApplicationTests {
	
	@Autowired
	private ShortUrlRepository repository;
	
	@Autowired
	private ShortUrlService service;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	private static final String DEFAULT_URL = "http://bemobi.com.br";
	private static final String DEFAULT_PATH = "/shorturl";
	private static final String DEFAULT_LABEL = "jedi";
	private static final String DEFAULT_LABEL_URL_PATH = "/u/jedi";
	
	private static final String INVALID_URL = "Test://bemobi.com.br";
	private static final String TOP_TEN_VIEWS_PATH = "/shorturl/topTenViews";
	
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		repository.deleteByShortUrlLabel(DEFAULT_LABEL);
	}
	
	@After
	public void after() {
		repository.deleteByShortUrlLabel(service.getLabel());
	}
	
	/**
	 * POST Scenarios
	 */
	
	@Test
	public void shouldCreateShortUrlWithoutCustomLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL);
		mockMvc.perform(requestBuilder).andExpect(status().isCreated())
		.andExpect(jsonPath("$.label").value(service.getLabel()))
		.andExpect(jsonPath("$.originalUrl").value(DEFAULT_URL));
	}
	
	@Test
	public void shouldCreateShortUrlWithCustomLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.post(DEFAULT_PATH).param("url", DEFAULT_URL).param("custom_label", DEFAULT_LABEL);
		
		mockMvc.perform(requestBuilder).andExpect(status().isCreated())
		.andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
		.andExpect(jsonPath("$.originalUrl").value(DEFAULT_URL));
		
		assertNotNull(repository.findByShortUrlLabel(DEFAULT_LABEL));
	}
	
	@Test
	public void shouldNotCreateShortUrlWithCustomLabelIfExistsTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL)
				.param("custom_label", DEFAULT_LABEL);
		
		mockMvc.perform(requestBuilder).andExpect(status().isCreated());
		
		mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
		.andExpect(jsonPath("$.error_code").value(ErrorCodes.CUSTOM_LABEL_ALREADY_EXISTS_ERROR_CODE))
		.andExpect(jsonPath("$.description").value(ErrorCodes.CUSTOM_LABEL_ALREADY_EXISTS_ERROR_DESCRIPTION));
	}
	
	@Test
	public void expectBadRequestIfTryToCreateShortUrlWithInvalidUrlTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", INVALID_URL);
		mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.label").value(""))
		.andExpect(jsonPath("$.error_code").value(ErrorCodes.INVALID_URL_FORMAT_ERROR_CODE))
		.andExpect(jsonPath("$.description").value(ErrorCodes.INVALID_URL_FORMAT_ERROR_DESCRIPTION));
	}
	
	 /** 
	 * GET Scenarios
	 * 
	 */
	
	@Test
	public void shouldReturnNotFoundWithEmptyLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(DEFAULT_PATH).param("label", "");
		
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound())
		.andExpect(jsonPath("$.label").value(""))
		.andExpect(jsonPath("$.error_code").value(ErrorCodes.SHORTENED_URL_NOT_FOUND_ERROR_CODE))
		.andExpect(jsonPath("$.description").value(ErrorCodes.SHORTENED_URL_NOT_FOUND_ERROR_DESCRIPTION));
	}
	
	@Test
	public void shouldReturnBadRequestWithNullLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(DEFAULT_PATH);
		mockMvc.perform(requestBuilder).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.label").value(""))
		.andExpect(jsonPath("$.error_code").value(ErrorCodes.UNINFORMED_LABEL_ERROR_CODE))
		.andExpect(jsonPath("$.description").value(ErrorCodes.UNINFORMED_LABEL_ERROR_DESCRIPTION));
	}
			
	
	@Test
	public void shouldReturnCreatedLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH)
															.param("url", DEFAULT_URL)
															.param("custom_label", DEFAULT_LABEL);
		mockMvc.perform(requestBuilder).andExpect(status().isCreated())
		.andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
		.andExpect(jsonPath("$.originalUrl").value(DEFAULT_URL));
		
		requestBuilder = MockMvcRequestBuilders.get(DEFAULT_PATH).param("label", DEFAULT_LABEL);
		mockMvc.perform(requestBuilder).andExpect(status().isOk())
		.andExpect(jsonPath("$.shortUrlLabel").value(DEFAULT_LABEL))
		.andExpect(jsonPath("$.originalUrl").value(DEFAULT_URL))
		.andExpect(jsonPath("$.views").value(0));
	}
	
	/**
	 * REDIRECT TESTES
	 */
	
	@Test
	public void shouldRedirectToCreatedLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL)
				.param("custom_label", DEFAULT_LABEL);
		mockMvc.perform(requestBuilder).andExpect(status().isCreated())
		.andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
		.andExpect(jsonPath("$.originalUrl").value(DEFAULT_URL));
		
		requestBuilder = MockMvcRequestBuilders.get(DEFAULT_LABEL_URL_PATH);
		mockMvc.perform(requestBuilder).andExpect(status().isFound()).andExpect(redirectedUrl(DEFAULT_URL));
	}
	
	@Test
	public void shouldReturnNotFoundIfShortUrlNotExistsTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/u/asdf");
		mockMvc.perform(requestBuilder).andExpect(status().isNotFound())
					.andExpect(jsonPath("$.label").value("asdf"))
					.andExpect(jsonPath("$.error_code").value(ErrorCodes.SHORTENED_URL_NOT_FOUND_ERROR_CODE))
					.andExpect(jsonPath("$.description").value(ErrorCodes.SHORTENED_URL_NOT_FOUND_ERROR_DESCRIPTION));
	}
	
	
	/** 
	 * TOP TEN TEST
	 */
	
	@Test
	public void shouldReturnTopTenTest() throws Exception {
		String label = "";
		RequestBuilder requestBuilder;
		//create 20 urls with custom label
		for (int i = 0; i < 20; i++) {
			label = DEFAULT_LABEL+i;
			requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL)
					.param("custom_label", label);
			mockMvc.perform(requestBuilder).andExpect(status().isCreated())
			.andExpect(jsonPath("$.label").value(label))
			.andExpect(jsonPath("$.originalUrl").value(DEFAULT_URL));
		}
		
		Random random = new Random();
		//access 10 urls
		for (int i = 0; i < 10; i++) {
			int randomNumber = random.nextInt((15 - 1)) + 1;
			for(int j = 0; j < randomNumber; j++) //visit urls
				label = DEFAULT_LABEL_URL_PATH+i;
				requestBuilder = MockMvcRequestBuilders.get(label);
				mockMvc.perform(requestBuilder).andExpect(status().isFound()).andExpect(redirectedUrl(DEFAULT_URL));
		}
		
		requestBuilder = MockMvcRequestBuilders.get(TOP_TEN_VIEWS_PATH);
		mockMvc.perform(requestBuilder).andExpect(status().isOk())
		.andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(10)));
		
		//remove created urls
		for (int i = 0; i < 20; i++) {
			label = DEFAULT_LABEL+i;
			repository.deleteByShortUrlLabel(label);
		}
	}
	
	
	
	@Test
	public void shouldGenerateUniqueHashesWithoutFailTest() {
		String hash;
		List<String> hashes = new ArrayList<>();
		for (int i = 1; i < 10000; i++) {
			hash = Base62.convertDecimalToBase62(i);
			if(hashes.contains(hash)) {
				fail();
			}
			hashes.add(hash);
		}
	}
	
	@Test
	public void whenNumberToConvertIsZeroExpectedZero() {
		assertEquals("0", Base62.convertDecimalToBase62(0));
	}

	@Test
	public void contextLoads() {
		
	}

}
