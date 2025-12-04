package com.org.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {


	@Bean
	public ObjectMapper objectMapper(){

		ObjectMapper objectMapper=new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		return objectMapper;
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
}
