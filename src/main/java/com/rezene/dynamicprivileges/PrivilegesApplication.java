package com.rezene.dynamicprivileges;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@SpringBootApplication
@ComponentScan(basePackages = {"com.rezene.dynamicprivileges"})
public class PrivilegesApplication {
	
	public static void main(String[] args) {
    SpringApplication.run(PrivilegesApplication.class, args);
	}
	
	@Bean
	public SessionRegistry sessionRegistry() {
	    return new SessionRegistryImpl();
	}

}
