package com.java.app.urllookupservice.utils;

public class URLUtil {
		
    public static String extractHost(String host) {
        if (host == null || host.length() == 0)
            return "";

        int dblSlash = host.indexOf("//");
        dblSlash = dblSlash == -1 ? 0 : dblSlash + 2;

        int end = host.indexOf(':', dblSlash);
        end = end >= 0 ? end : host.indexOf('/', dblSlash);
        end = end >= 0 ? end : host.length();
        return host.substring(dblSlash, end).replaceFirst("^www.*?\\.", "");
    }
    
    public static boolean isMallicious(String hostName) {
    	//HTTP Proxy or dtection API to identify whether it is malwate or not and then accordingly update BlackList DB
    	return true;
    }
}
