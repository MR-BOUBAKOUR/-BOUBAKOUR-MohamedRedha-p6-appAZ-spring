package com.payMyBuddy;

import com.payMyBuddy.config.EnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		EnvLoader.load();
		SpringApplication.run(Application.class, args);
	}

}
