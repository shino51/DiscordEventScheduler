package org.shino.handler;

import org.shino.model.dto.DiscordEventDTO;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
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
public class GetScheduledEventsHandler {

  @Autowired
  private DiscordEventDispatcher eventDispatcher;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @Value("${guild.id}")
  private String guildId;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${auth.token}")
  private String authToken;

  public List<DiscordEventDTO> run() {
    String url = discordApiUrl + "/guilds/" + guildId + "/scheduled-events";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", authToken);
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<List<DiscordEventDTO>> response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      request,
      new ParameterizedTypeReference<>() {
      }
    );
    return response.getBody();
  }
}
