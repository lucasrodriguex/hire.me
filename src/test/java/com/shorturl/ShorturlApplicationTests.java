package com.shorturl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.shorturl.repository.ShortUrlRepository;
import com.shorturl.util.Base62;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShorturlApplicationTests {
	
	@Autowired
	private ShortUrlRepository repository;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	private static final String DEFAULT_URL = "http://bemobi.com.br";
	private static final String DEFAULT_LABEL = "jedi";
	private static final String INVALID_URL = "Test://bemobi.com.br";
	private static final String DEFAULT_PATH = "/shorturl";
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		repository.deleteByShortUrlLabel(DEFAULT_LABEL);
	}
	
	/**
	 * POST Scenarios
	 */
	
	@Test
	public void shouldCreateShortUrlWithoutCustomLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL);
		MockHttpServletResponse response = getRequestResponseFromBuilder(requestBuilder);
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
	}
	
	@Test
	public void shouldCreateShortUrlWithCustomLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL)
				.param("custom_label", DEFAULT_LABEL);
		MockHttpServletResponse response = getRequestResponseFromBuilder(requestBuilder);
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		assertNotNull(repository.findByShortUrlLabel(DEFAULT_LABEL));
	}
	
	@Test
	public void shouldNotCreateShortUrlWithCustomLabelIfExistsTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL)
				.param("custom_label", DEFAULT_LABEL);
		MockHttpServletResponse response = getRequestResponseFromBuilder(requestBuilder);
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		
		requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL)
				.param("custom_label", DEFAULT_LABEL);
		response = getRequestResponseFromBuilder(requestBuilder);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}
	
	@Test
	public void expectBadRequestIfTryToCreateShortUrlWithInvalidUrlTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", INVALID_URL);
		MockHttpServletResponse response = getRequestResponseFromBuilder(requestBuilder);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}
	
	 /** 
	 * GET Scenarios
	 * 
	 */
	
	@Test
	public void shouldReturnNotFoundWithEmptyLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(DEFAULT_PATH).param("label", "");
		MockHttpServletResponse response = getRequestResponseFromBuilder(requestBuilder);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}
	
	@Test
	public void shouldReturnBadRequestWithNullLabelTest() throws Exception {
		String label = null;
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(DEFAULT_PATH).param("label", label);
		MockHttpServletResponse response = getRequestResponseFromBuilder(requestBuilder);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}
			
	
	@Test
	public void shouldReturnCreatedLabelTest() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(DEFAULT_PATH).param("url", DEFAULT_URL)
				.param("custom_label", DEFAULT_LABEL);
		mockMvc.perform(requestBuilder);
		
		requestBuilder = MockMvcRequestBuilders.get(DEFAULT_PATH).param("label", DEFAULT_LABEL);
		mockMvc.perform(requestBuilder).andExpect(jsonPath("$.shortUrlLabel").value(DEFAULT_LABEL))
		.andExpect(jsonPath("$.originalUrl").value(DEFAULT_URL))
		.andExpect(jsonPath("$.views").value(0));
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
	
	private MockHttpServletResponse getRequestResponseFromBuilder(RequestBuilder requestBuilder) throws Exception {
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		MockHttpServletResponse response = result.getResponse();
		return response;
	}

	@Test
	public void contextLoads() {
		
	}

}
