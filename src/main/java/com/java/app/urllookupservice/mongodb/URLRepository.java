package com.java.app.urllookupservice.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface URLRepository extends MongoRepository<URL, String>{
	
	public URL findOneByHostName(String hostName);

}
