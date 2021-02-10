package com.alauda.ms.riverland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RiverlandApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiverlandApplication.class, args);
	}

}
