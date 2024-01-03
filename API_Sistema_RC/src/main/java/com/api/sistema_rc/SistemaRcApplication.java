package com.api.sistema_rc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaRcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaRcApplication.class, args);
	}

}
