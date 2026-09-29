package com.upc.products.config;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean // se inyectará en cualquier clase que lo necesite
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}