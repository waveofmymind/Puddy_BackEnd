package com.team.puddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
public class PuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PuddyApplication.class, args);

	}

}
