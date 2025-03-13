package edu.eci.arsw.blueprintsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "edu.eci.arsw")
public class BlueprintsAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlueprintsAPIApplication.class, args);
	}
}
