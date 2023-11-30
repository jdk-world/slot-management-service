package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.swagger2.annotations.EnableSwagger2;






@SpringBootApplication
@ComponentScan({ "com.example" })
@EnableSwagger2
public class AppMainSlotMgmtSvc extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AppMainSlotMgmtSvc.class);
	}

	public static void main(String[] args) {
		ApplicationHome home = new ApplicationHome(AppMainSlotMgmtSvc.class);
		SpringApplication.run(AppMainSlotMgmtSvc.class, args);
	}

}

