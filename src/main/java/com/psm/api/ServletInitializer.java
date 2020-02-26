package com.psm.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.HiddenHttpMethodFilter;

public class ServletInitializer extends SpringBootServletInitializer {

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(PsmapiApplication.class);
//	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ServletInitializer.class);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ServletInitializer.class, args);
	}
	
  /**
  * HiddenHttpMethodFilter  
  */
 @Bean
 public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
     HiddenHttpMethodFilter filter = new HiddenHttpMethodFilter();
     return filter;
 }
 
 @Bean
 public PasswordEncoder passwordEncoder() {
     return PasswordEncoderFactories.createDelegatingPasswordEncoder();
 }
}
