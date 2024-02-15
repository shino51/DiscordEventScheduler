package org.shino.handler.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shino.model.dto.DiscordEventDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiscordEventDispatcher {

  @Value("${auth.token}")
  private String authToken;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @Value("${guild.id}")
  private String guildId;

  private final String baseURL = discordApiUrl + "/guilds/" + guildId + "/";

  private final RestTemplate restTemplate;

  public List<DiscordEventDTO> getRequest(String tailedUrl) {
    String url = baseURL + tailedUrl;

    ResponseEntity<List<DiscordEventDTO>> response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      new HttpEntity<>(createHeader()),
      new ParameterizedTypeReference<>() {
      }
    );
    return response.getBody();
  }

  public DiscordEventDTO postRequest(String tailedUrl, DiscordEventDTO body) {
    String url = baseURL + tailedUrl;

    HttpEntity<DiscordEventDTO> requestEntity = new HttpEntity<>(body, createHeader());
    try {
      ResponseEntity<DiscordEventDTO> response = restTemplate.postForEntity(
        url,
        requestEntity,
        DiscordEventDTO.class
      );
      return response.getBody();
    } catch (HttpClientErrorException exception) {
      if (exception.getMessage().contains("You are being rate limited")) {
        try {
          TimeUnit.MINUTES.sleep(1);
          return postRequest(tailedUrl, body);
        } catch(InterruptedException interruptedException) {
          log.error("unable to hold the thread. " + interruptedException.getMessage());
          Thread.currentThread().interrupt();
          return DiscordEventDTO.builder()
            .guildScheduledEventExceptions(Arrays.asList(exception.getMessage(), interruptedException.getMessage()))
            .build();
        }
      } else {
        return DiscordEventDTO.builder()
          .guildScheduledEventExceptions(Collections.singletonList(exception.getMessage()))
          .build();
      }
    }
  }

  public String deleteRequest(String tailedUrl) {
    String url = discordApiUrl + "/guilds/" + guildId + "/" + tailedUrl;
    restTemplate.delete(url);
    return "Delete Request has successfully been completed. URL: " + url;
  }

  private HttpHeaders createHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", authToken);
    return headers;
  }
}
