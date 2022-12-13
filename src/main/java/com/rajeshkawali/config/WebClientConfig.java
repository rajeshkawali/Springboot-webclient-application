package com.rajeshkawali.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Rajesh_Kawali
 * 
 */
@Configuration
public class WebClientConfig {

	@Value("${api.base.url}")
    private String baseUrl;
	
    @Bean
    WebClient webClient(WebClient.Builder webClientBuilder) {
    	return webClientBuilder.baseUrl(baseUrl).build();
    }
}