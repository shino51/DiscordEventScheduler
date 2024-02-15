package org.shino.handler.dispatcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shino.model.dto.DiscordEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiscordEventDispatcherTest {

  private MockRestServiceServer mockServer;

  @Autowired
  @InjectMocks
  private DiscordEventDispatcher eventDispatcher;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @Value("${guild.id}")
  private String guildId;

  @Mock
  private RestTemplate restTemplate;

  @Test
  public void testPostRequest() {
    String tailedUrl = "scheduled-events";
    String expectedUrl = discordApiUrl + "/guilds/" + guildId + "/" + tailedUrl;

    DiscordEventDTO responseBody = createResponseDTO();

    ResponseEntity<DiscordEventDTO> response = new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
    when(restTemplate.postForEntity(eq(expectedUrl), any(HttpEntity.class), eq(DiscordEventDTO.class)))
      .thenReturn(response);

    DiscordEventDTO result = eventDispatcher.postRequest(tailedUrl, createEventDTO());

    assertThat(result).isEqualTo(responseBody);
  }

  @Test
  public void testGetRequest() {
    String tailedUrl = "scheduled-events";
    String expectedUrl = discordApiUrl + "/guilds/" + guildId + "/" + tailedUrl;

    var responseBody = Collections.singletonList(createResponseDTO());
    ResponseEntity<List<DiscordEventDTO>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

    when(restTemplate.exchange(
      eq(expectedUrl),
      eq(HttpMethod.GET),
      any(HttpEntity.class),
      any(ParameterizedTypeReference.class)))
      .thenReturn(response);

    List<DiscordEventDTO> result = eventDispatcher.getRequest(tailedUrl);

    assertThat(result).isEqualTo(responseBody);
  }


  private DiscordEventDTO createEventDTO() {

    LocalDateTime jpnDateTime = LocalDateTime.parse("2025-02-10T21:00:00");
    ZonedDateTime utcDateTime = ZonedDateTime.of(jpnDateTime, ZoneId.of("JST", ZoneId.SHORT_IDS)).withZoneSameInstant(ZoneOffset.UTC);

    return DiscordEventDTO.builder()
      .channelId("1041441815854854184")
      .name("test")
      .description("description")
      .scheduledStartTime(utcDateTime)
      .build();
  }

  private DiscordEventDTO createResponseDTO() {
    return DiscordEventDTO.builder()
      .id("11111111222222")
      .channelId("1055555555555")
      .guildId(guildId).build();
  }
}