package org.shino.spring.configuration;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
@PropertySource("classpath:discord.properties")
public class Configuration {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    RestTemplate restTemplate = builder.build();
//    List<HttpMessageConverter<?>> converters = new ArrayList<>();
//    converters.add(new StringHttpMessageConverter());
//    converters.add(new GsonHttpMessageConverter());
//    restTemplate.setMessageConverters(converters);

    return restTemplate;
  }

}
