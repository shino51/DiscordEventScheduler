package org.shino.handler.dispatcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shino.exception.DiscordEventDispatcherException;
import org.shino.model.DiscordEventRecord;
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

  private final RestTemplate restTemplate;

  @Value("${auth.token}")
  private String authToken;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @Value("${guild.id}")
  private String guildId;

  private String baseUrl;

  private static final int MAX_RETRY_COUNT = 5;

  public List<DiscordEventRecord> getRequest(String tailedUrl) {
    String url = getBaseUrl() + tailedUrl;

    ResponseEntity<List<DiscordEventRecord>> response = restTemplate.exchange(
      url,
      HttpMethod.GET,
      new HttpEntity<>(createHeader()),
      new ParameterizedTypeReference<>() {
      }
    );
    return response.getBody();
  }

  public DiscordEventRecord postRequest(String tailedUrl, DiscordEventRecord body, int retryCount) throws DiscordEventDispatcherException {
    String url = getBaseUrl() + tailedUrl;

    HttpEntity<DiscordEventRecord> requestEntity = new HttpEntity<>(body, createHeader());
    try {
      ResponseEntity<DiscordEventRecord> response = restTemplate.postForEntity(
        url,
        requestEntity,
        DiscordEventRecord.class
      );
      return response.getBody();
    } catch (HttpClientErrorException exception) {
      if (exception.getMessage().contains("You are being rate limited")) {
        try {
          if (retryCount == MAX_RETRY_COUNT) {
            throw new DiscordEventDispatcherException("", exception);
          }
          TimeUnit.SECONDS.sleep(10);
          return postRequest(tailedUrl, body, ++retryCount);
        } catch(InterruptedException interruptedException) {
          log.error("unable to hold the thread. " + interruptedException.getMessage());
          Thread.currentThread().interrupt();
          return DiscordEventRecord.builder()
            .guildScheduledEventExceptions(Arrays.asList(exception.getMessage(), interruptedException.getMessage()))
            .build();
        }
      } else {
        return DiscordEventRecord.builder()
          .guildScheduledEventExceptions(Collections.singletonList(exception.getMessage()))
          .build();
      }
    }
  }

  public void deleteRequest(String tailedUrl) {
    String url = getBaseUrl() + tailedUrl;
    restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(createHeader()), String.class);
  }

  private HttpHeaders createHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", authToken);
    return headers;
  }

  private String getBaseUrl() {
    if (baseUrl == null) {
      baseUrl = discordApiUrl + "/guilds/" + guildId + "/";
    }
    return baseUrl;
  }
}
