package com.java.app.urllookupservice;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.java.app.urllookupservice.controller.URLLookupController;

//@AutoConfigureMockMvc
public class UrlLookupRestAppTests extends UrlLookupServiceApplicationTests {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	//@Autowired
	private MockMvc mockMvc;
	
	@InjectMocks
	private URLLookupController urlLookupController;
	
	@Before
	public void setUP() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	//################# Adding 1st Malicious URL(www.malliciousURL.co.in) into BlackList DB ##############
	// GET /add-to-blacklist/www.malliciousURL.co.in/user=rahul&empid=01234
	//####################################################################################################
	@Test
	public void testAddURLtoBlackListDB1() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/add-to-blacklist/www.malliciousURL.co.in/user=rahul&empid=01234").accept(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isOk())
			//.andExpect(MockMvcResultMatchers.jsonPath("$.hostName").value("malliciousURL.co.in"))
			//.andExpect(MockMvcResultMatchers.jsonPath("$.severity").value(1))
			//.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(Matchers.contains("Malicious URL has beed added explicitly")));
			.andExpect(MockMvcResultMatchers.content().string("{\"hostName\":\"malliciousURL.co.in\",\"fullURL\":\"user=rahul&empid=01234\",\"severity\":1,\"description\":\"Malicious URL has beed added explicitly\"}"));
	}

	//################# Adding 2nd Malicious URL(www.whalidgroup.com) into BlackList DB ##################
	// GET /add-to-blacklist/www.whalidgroup.com/user=rahul&empid=01234
	//####################################################################################################
	@Test
	public void testAddURLtoBlackListDB2() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/add-to-blacklist/www.whalidgroup.com/user=rahul&empid=01234"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("{\"hostName\":\"whalidgroup.com\",\"fullURL\":\"user=rahul&empid=01234\",\"severity\":1,\"description\":\"Malicious URL has beed added explicitly\"}"));
	}
	
	//################# URL lookup for Malicious URL (whalidgroup.com) into BlackList DB #################
	// GET /urlinfo/1/whalidgroup.com/user=rahul&empid=04123/
	//####################################################################################################
	@Test
	public void testLookupMaliciousURL() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/urlinfo/1/whalidgroup.com/user=rahul&empid=04123/"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andExpect(MockMvcResultMatchers.content().string("{\"result\":[{\"severity\":1,\"hostName\":\"whalidgroup.com\",\"full-url\":\"user=rahul&empid=04123\",\"description\":\"Malicious URL\"}],\"success\":false,\"message\":\"Malicious URL present in Blacklist DB\"}"));
	}
	
	//################# URL lookup for Good URL (www.google.com) into BlackList DB ######################
	// GET /urlinfo/1/www.google.com/user=rahul&empid=04123/
	//####################################################################################################
	@Test
	public void testLookupGoodURL() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/urlinfo/1/www.google.com/user=rahul&empid=04123/"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("{\"result\":[{\"severity\":0,\"hostName\":\"google.com\",\"full-url\":\"user=rahul&empid=04123\",\"description\":\"URL not found in the database, need to be scanned..\"}],\"success\":true}"));
	}
	
	//##### Now marking a blacklisted URLl(www.whalidgroup.com) as whitelisted in BlackList DB ###########
	// GET /markAsWhiteListed/whalidgroup.com/user=rahul&empid=04123/
	//####################################################################################################
	@Test
	public void testMarkAsWhiteListedURL() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/markAsWhiteListed/whalidgroup.com/user=rahul&empid=04123/"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("{\"hostName\":\"whalidgroup.com\",\"fullURL\":\"user=rahul&empid=04123\",\"severity\":0,\"description\":\"URL has been marked as whitelisted explicitly\"}"));
	}
	
	//##### Now URL lookup for the URL which was marked as whitelisted in BlackList DB ###################
	// GET /urlinfo/1/whalidgroup.com/user=rahul&empid=04123/
	//####################################################################################################
	@Test
	public void testLookupWhiteListedURL() throws Exception {
		
		//Marking as whitelist
		mockMvc.perform(MockMvcRequestBuilders.get("/markAsWhiteListed/whalidgroup.com/user=rahul&empid=04123/"))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().string("{\"hostName\":\"whalidgroup.com\",\"fullURL\":\"user=rahul&empid=04123\",\"severity\":0,\"description\":\"URL has been marked as whitelisted explicitly\"}"));

		//Now URL lookup for the URL which was marked as whitelisted in BlackList DB
		mockMvc.perform(MockMvcRequestBuilders.get("/urlinfo/1/whalidgroup.com/user=rahul&empid=04123/"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.content().string("{\"result\":[{\"severity\":0,\"hostName\":\"whalidgroup.com\",\"full-url\":\"user=rahul&empid=04123\",\"description\":\"URL has been marked as whitelisted explicitly\"}],\"success\":true}"));
	}
	
}
