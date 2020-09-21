package com.getxinfo.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

@SpringBootApplication
public class AchillesContentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AchillesContentApplication.class, args);
	}

}
