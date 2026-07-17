package com.hen.flastcard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FlastcardApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlastcardApplication.class, args);
	}

}
