package com.ksmartpia.sisbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SisBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SisBatchApplication.class, args);
	}

}
