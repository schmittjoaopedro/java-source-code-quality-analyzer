package com.github.schmittjoaopedro.database;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;

@Configuration
public class DatabaseConfiguration {
	
	public @Bean MongoClient mongoClient() {
		return new MongoClient("brjgsd309173");
	}
	
	public @Bean MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), "source_code_metrics");
	}

}
