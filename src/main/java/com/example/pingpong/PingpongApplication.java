package com.example.pingpong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PingpongApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingpongApplication.class, args);
	}

}
