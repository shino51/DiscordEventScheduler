package org.shino.handler.dispatcher;

import org.shino.model.dto.CreateEventDTO;
import org.shino.model.dto.DiscordEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class DiscordEventDispatcher {

  @Value("${auth.token}")
  private String authToken;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @Value("${guild.id}")
  private String guildId;

  @Autowired
  private RestTemplate restTemplate;

  public List<DiscordEventDTO> getRequest(String tailedUrl) {
    String url = discordApiUrl + "/guilds/" + guildId + "/" + tailedUrl;

    ResponseEntity<List<DiscordEventDTO>> response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      new HttpEntity<>(createHeader()),
      new ParameterizedTypeReference<>() {
      }
    );
    return response.getBody();
  }

  public DiscordEventDTO postRequest(String tailedUrl, CreateEventDTO body) {
    String url = discordApiUrl + "/guilds/" + guildId + "/" + tailedUrl;

    HttpEntity<CreateEventDTO> requestEntity = new HttpEntity<>(body, createHeader());
    ResponseEntity<DiscordEventDTO> response = restTemplate.postForEntity(
      url,
      requestEntity,
      DiscordEventDTO.class
    );
    return response.getBody();
  }

  private HttpHeaders createHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", authToken);
    return headers;
  }
}
