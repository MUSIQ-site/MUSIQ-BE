package com.a608.musiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MusiqApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusiqApplication.class, args);
	}

}
