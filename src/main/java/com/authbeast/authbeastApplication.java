package com.zentois.authbeast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
@ComponentScan(basePackages = {"com.zentois"}) 
public class authbeastApplication
{	
	public static void main(String[] args)
	{
		SpringApplication.run(authbeastApplication.class, args);
	}
}