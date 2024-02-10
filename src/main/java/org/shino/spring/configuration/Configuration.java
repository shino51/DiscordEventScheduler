package org.shino.spring.configuration;


import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@SpringBootConfiguration
public class Configuration {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public DataSource dataSource() {

    // no need shutdown, EmbeddedDatabaseFactoryBean will take care of this
    return new EmbeddedDatabaseBuilder()
      .setType(EmbeddedDatabaseType.HSQL)
      .addScripts("db/schema.sql", "db/data.sql")
      .setName("discord")
      .build();
  }
}
