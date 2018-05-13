package com.java.app.urllookupservice.mongodb;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = URLRepository.class)
@Configuration
public class MongoDBConfig {

}
