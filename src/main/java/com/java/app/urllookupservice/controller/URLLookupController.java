package com.java.app.urllookupservice.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.java.app.urllookupservice.mongodb.URL;
import com.java.app.urllookupservice.mongodb.URLRepository;
import com.java.app.urllookupservice.utils.URLUtil;

@RestController
public class URLLookupController {
	@Autowired
	URLRepository urlRepository;
	
	@RequestMapping(path = "/urlinfo/1/{host}/{full_url}", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public String lookup(@PathVariable("host") String host, @PathVariable("full_url") String fullUrl, HttpServletResponse response) {
		URL urlObj = null;
        if (host != null && !host.isEmpty() && fullUrl != null && !fullUrl.isEmpty()) {
            
    		//extract HostName URL
    		String hostName = URLUtil.extractHost(host);
    		System.out.println("host name.." + hostName);
    	
    		//Check whethere the URL is already blacklisted or not
            URL foundInList = urlRepository.findOneByHostName(hostName);
    		System.out.println("foundInList.." + foundInList);
            
            //Case: If the URL has not been blacklisted so far
            if(foundInList == null || (foundInList != null && foundInList.getSeverity() == 0) ) {
            	
            	//###################### Case 1: ##################################
            	
            	// URL is not in Blacklist DB
            	// It depends on the customer's expectation, this can be kept as WhiteListDB
        		if(foundInList != null && foundInList.getSeverity() == 0) {
        			response.setStatus(HttpStatus.OK.value());
	        		return toJSONString(true,foundInList);
        		} else {
        			String desc = "URL not found in the database, need to be scanned..";
            		urlObj = new URL(hostName, fullUrl, URL.WHITELIST, desc/*, (new Date()).toString()*/);
            		
	        		//subject to the discussion whether this will be saved or not
            		//urlRepository.save(urlObj); 
	        		
            		response.setStatus(HttpStatus.OK.value());
	        		return toJSONString(true,urlObj);
        		}
        		
        		//###################### Case 2:  ##################################
            	//Check through HTTP Proxy which is URL checker, 
            	//to check whether URL is mallicious or not, then update the BlackListDB
            	
            	/*
            	boolean isMallicious = URLUtil.isMallicious(url);
            	
            	//if mallicious update into BlackList DB
            	if(isMallicious) {
            		urlObj = new URLs(hostName, fullUrl, URLs.MALWARE, "Mallicious URL");
            		urlRepository.save(urlObj);
            		response.setStatus(HttpStatus.BAD_REQUEST.value());
            		return toJSONString(false,urlObj);
            	} else {
            		// URL is not Blacklisted
            		//If the requirement says that we have toto the same the same DB, theen uncomment 
            		urlObj = new URLs(hostName, fullUrl, URLs.GOODWARE, "Goodware");
            		response.setStatus(HttpStatus.OK.value());
            		return toJSONString(true,urlObj);
            	}
            	*/
            	
            } else if (foundInList != null && foundInList.getSeverity() == 1)  {
            	//URL already black listed,
            	urlObj = new URL(hostName, fullUrl, URL.BLACKLIST, "Malicious URL" /*, (new Date()).toString()*/);
        		urlRepository.save(urlObj);
        		response.setStatus(HttpStatus.BAD_REQUEST.value());
        		return toJSONString(false,urlObj);
            }
        }
        
        System.out.println("No Content..");
        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        return "{\"success\": \"false\", \"description\":\"Wrong Input Format\"}" ;
    }	
	
	@GetMapping("/all-urls")
	public List<URL> getAllURLs(){
		System.out.println("Searching All URL - ");
		return urlRepository.findAll();
	}
	
	@GetMapping("/aurl/{host}")
	public URL getURL(@PathVariable("host") String host){
		System.out.println("Searching host - " +host);
		String hostName = URLUtil.extractHost(host);
		return urlRepository.findOneByHostName(hostName);
	}
	
	@GetMapping("/add-to-blacklist/{host}/{full_url}")
	public URL addURLtoBlackListDB(@PathVariable("host") String host,
			@PathVariable("full_url") String fullURL) {
		System.out.println("Searching host - " +host);
		String hostName = URLUtil.extractHost(host);
		URL urlObj = new URL(hostName, fullURL, URL.BLACKLIST, "Malicious URL has beed added explicitly"/*, (new Date()).toString()*/);
		return urlRepository.save(urlObj);
	}
	
	@GetMapping("/markAsWhiteListed/{host}/{full_url}")
	public URL markAsWhiteListed(@PathVariable("host") String host,
			@PathVariable("full_url") String fullURL) {
		System.out.println("Searching host - " +host);
		String hostName = URLUtil.extractHost(host);
		URL urlObj = new URL(hostName, fullURL, URL.WHITELIST, "URL has been marked as whitelisted explicitly"/*, (new Date()).toString()*/);
		return urlRepository.save(urlObj);
	}
	
	
	private String toJSONString(boolean status, URL url) {
		
		JSONObject json = new JSONObject();
		try {
			json.put("success", status);

			JSONArray array = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("hostName", url.getHostName());
			item.put("full-url", url.getFullURL());
			item.put("severity", url.getSeverity());
			item.put("description", url.getDescription());
			//item.put("modifiedDate", url.getModifiedDate());
			
			array.put(item);
			json.put("result", array);
			
			if(!status) {
				json.put("message", "Malicious URL present in Blacklist DB");
			}
			
		} catch (Exception e) {
			return "{\"success\": \"false\", \"description\":\"Exception forming JSON\"}" ;
		}

		return json.toString();
	}

}
