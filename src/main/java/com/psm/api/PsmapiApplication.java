//package com.psm.api;
//
//import java.util.TimeZone;
//
//import javax.annotation.PostConstruct;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.filter.HiddenHttpMethodFilter;
//@EnableJpaAuditing
//@EntityScan(basePackageClasses = {Jsr310JpaConverters.class}, basePackages = {"com.psm.api"})
//@SpringBootApplication(exclude= {MultipartAutoConfiguration.class})
//@EnableScheduling
//public class PsmapiApplication {
//	
//	public static void main(String[] args) {
//		SpringApplication.run(PsmapiApplication.class, args);
//	}
//	
//    /**
//     * HiddenHttpMethodFilter  
//     */
//    @Bean
//    public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
//        HiddenHttpMethodFilter filter = new HiddenHttpMethodFilter();
//        return filter;
//    }
//    
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    } 
//
//    
//    
//    @Bean
//    public TaskScheduler taskScheduler() {
//
//        return new ConcurrentTaskScheduler();
//    }
//
//    
//    
//    
//
//}
