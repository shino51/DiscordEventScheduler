package org.shino.handler.dispatcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shino.repository.model.dto.CreateEventDTO;
import org.shino.repository.model.dto.DiscordEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.*;

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

    DiscordEventDTO responseBody = createDiscordEventDTO();

    ResponseEntity<DiscordEventDTO> response = new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
    when(restTemplate.postForEntity(eq(expectedUrl), any(HttpEntity.class), eq(DiscordEventDTO.class)))
      .thenReturn(response);

    DiscordEventDTO result = eventDispatcher.postRequest(tailedUrl, createEventDTO());

    assertThat(result).isEqualTo(responseBody);
  }

  private CreateEventDTO createEventDTO() {

    LocalDateTime jpnDateTime = LocalDateTime.parse("2025-02-10T21:00:00");
    ZonedDateTime zonedDateTime = ZonedDateTime.of(jpnDateTime, ZoneId.of("JST", ZoneId.SHORT_IDS));
    LocalDateTime utcDateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneOffset.UTC);

    return CreateEventDTO.builder()
      .channelId("1041441815854854184")
      .name("test")
      .description("description")
      .scheduledStartTime(utcDateTime)
      .build();
  }

  private DiscordEventDTO createDiscordEventDTO() {
    return DiscordEventDTO.builder()
      .id("1193598348742115329")
      .channelId("1041441815854854184")
      .guildId(guildId).build();
  }
}