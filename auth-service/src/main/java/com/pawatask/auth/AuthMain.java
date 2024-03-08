package com.pawatask.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthMain {
	public static void main(String[] args) {
		SpringApplication.run(AuthMain.class, args);
	}
}
