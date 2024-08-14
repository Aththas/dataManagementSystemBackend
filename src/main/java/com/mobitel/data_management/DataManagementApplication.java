package com.mobitel.data_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataManagementApplication.class, args);
	}

}
