package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.example.demo.dao.TableDao;

@Configuration
@ComponentScan("com.example.demo")
public class MyConfig {
	@Bean
	public TableDao getUserDao() {
		return new TableDao();
	}
}
