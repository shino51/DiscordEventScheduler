package org.shino.handler.dispatcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shino.exception.DiscordEventDispatcherException;
import org.shino.model.DiscordEventRecord;
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
  public void testPostRequest() throws DiscordEventDispatcherException {
    String tailedUrl = "scheduled-events";
    String expectedUrl = discordApiUrl + "/guilds/" + guildId + "/" + tailedUrl;

    DiscordEventRecord responseBody = createResponseDTO();

    ResponseEntity<DiscordEventRecord> response = new ResponseEntity<>(responseBody, HttpStatus.ACCEPTED);
    when(restTemplate.postForEntity(eq(expectedUrl), any(HttpEntity.class), eq(DiscordEventRecord.class)))
      .thenReturn(response);

    DiscordEventRecord result = eventDispatcher.postRequest(tailedUrl, createEventDTO(), 0);

    assertThat(result).isEqualTo(responseBody);
  }

  @Test
  public void testGetRequest() {
    String tailedUrl = "scheduled-events";
    String expectedUrl = discordApiUrl + "/guilds/" + guildId + "/" + tailedUrl;

    var responseBody = Collections.singletonList(createResponseDTO());
    ResponseEntity<List<DiscordEventRecord>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

    when(restTemplate.exchange(
      eq(expectedUrl),
      eq(HttpMethod.GET),
      any(HttpEntity.class),
      any(ParameterizedTypeReference.class)))
      .thenReturn(response);

    List<DiscordEventRecord> result = eventDispatcher.getRequest(tailedUrl);

    assertThat(result).isEqualTo(responseBody);
  }


  private DiscordEventRecord createEventDTO() {

    LocalDateTime jpnDateTime = LocalDateTime.parse("2025-02-10T21:00:00");
    ZonedDateTime utcDateTime = ZonedDateTime.of(jpnDateTime, ZoneId.of("JST", ZoneId.SHORT_IDS)).withZoneSameInstant(ZoneOffset.UTC);

    return DiscordEventRecord.builder()
      .channelId("1041441815854854184")
      .name("test")
      .description("description")
      .scheduledStartTime(utcDateTime)
      .build();
  }

  private DiscordEventRecord createResponseDTO() {
    return DiscordEventRecord.builder()
      .id("11111111222222")
      .channelId("1055555555555")
      .guildId(guildId).build();
  }
}