package org.shino.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
import org.shino.model.Event;
import org.shino.model.Frequency;
import org.shino.model.dto.DiscordEventDTO;
import org.shino.model.vo.CreateEventVO;
import org.shino.repository.EventRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateEventWeeklyHandlerTest {

  private static final String CHANNEL_ID = "123456";
  private static final String GUILD_ID = "545655";
  private static final Integer START_TIME = 21;
  private static final String NAME = "name of the event";
  private static final String DESCRIPTION = "description";

  private static final LocalDate FIRST_DAY_OF_MONTH_IN_WINTER_TIME = LocalDate.of(2024, 3, 1);
  private static final LocalDate FIRST_DAY_OF_MONTH_IN_SUMMER_TIME = LocalDate.of(2024, 6, 1);
  private static final LocalDate FIRST_DAY_OF_MONTH_SUNDAY = LocalDate.of(2024, 9, 1);
  private static final String JAPAN_TIMEZONE = "JST";
  private static final String EUROPE_PARIS_TIMEZONE = "ECT";

  @Captor
  ArgumentCaptor<DiscordEventDTO> argumentCaptor = ArgumentCaptor.forClass(DiscordEventDTO.class);

  @Mock
  private DiscordEventDispatcher dispatcher;

  @Mock
  private EventRepository repository;

  @InjectMocks
  private CreateEventWeeklyHandler createEventWeeklyHandler;

  @Test
  public void testCreateWeeklyEventInJpWinterTime() {
    // initiate mock's behaviour
    List<Event> events = Collections.singletonList(sundayEvent(JAPAN_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(dispatcher.postRequest(anyString(), any(DiscordEventDTO.class))).thenReturn(createDiscordEventDTO());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_WINTER_TIME);
    List<DiscordEventDTO> result = createEventWeeklyHandler.run(vo);

    // discord event DTO gets created
    assertThat(result).isNotNull().hasSize(5);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture());
    List<DiscordEventDTO> createEventDTOList = argumentCaptor.getAllValues();

    // validate contents of the first dto
    var firstCreatedDTO = createEventDTOList.get(0);
    assertThat(firstCreatedDTO.getChannelId()).isEqualTo(CHANNEL_ID);
    assertThat(firstCreatedDTO.getDescription()).isEqualTo(DESCRIPTION);
    assertThat(firstCreatedDTO.getName()).isEqualTo(NAME);
    // The first sunday in March 2024 is 3.3 and JST 21:00 is UTC 12:00
    assertThat(firstCreatedDTO.getScheduledStartTime()).isEqualTo("2024-03-03T12:00:00");

    // validate event dates for the rest of dtos
    assertThat(createEventDTOList.get(1).getScheduledStartTime()).isEqualTo("2024-03-10T12:00:00");
    assertThat(createEventDTOList.get(2).getScheduledStartTime()).isEqualTo("2024-03-17T12:00:00");
    assertThat(createEventDTOList.get(3).getScheduledStartTime()).isEqualTo("2024-03-24T12:00:00");
    assertThat(createEventDTOList.get(4).getScheduledStartTime()).isEqualTo("2024-03-31T12:00:00");
  }

  @Test
  public void createWeeklyEventForEuropeInWinterTime() {
    // initiate mock's behaviour
    List<Event> events = Collections.singletonList(sundayEvent(EUROPE_PARIS_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(dispatcher.postRequest(anyString(), any(DiscordEventDTO.class))).thenReturn(createDiscordEventDTO());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_WINTER_TIME);
    createEventWeeklyHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture());
    var createEventDTOList = argumentCaptor.getAllValues();
    // -1 hours offset
    assertThat(createEventDTOList.get(0).getScheduledStartTime()).isEqualTo("2024-03-03T20:00:00");
    assertThat(createEventDTOList.get(1).getScheduledStartTime()).isEqualTo("2024-03-10T20:00:00");
    assertThat(createEventDTOList.get(2).getScheduledStartTime()).isEqualTo("2024-03-17T20:00:00");
    assertThat(createEventDTOList.get(3).getScheduledStartTime()).isEqualTo("2024-03-24T20:00:00");
    // on the last day is the end of winter time. From this day, offset is -2 instead of -1
    assertThat(createEventDTOList.get(4).getScheduledStartTime()).isEqualTo("2024-03-31T19:00:00");
  }

  @Test
  public void createWeeklyEventForJapanInSummerTime() {
    // initiate mock's behaviour
    List<Event> events = Collections.singletonList(sundayEvent(JAPAN_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(dispatcher.postRequest(anyString(), any(DiscordEventDTO.class))).thenReturn(createDiscordEventDTO());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_SUMMER_TIME);
    createEventWeeklyHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture());
    var createEventDTOList = argumentCaptor.getAllValues();
    // +9 hours offset
    assertThat(createEventDTOList.get(0).getScheduledStartTime()).isEqualTo("2024-06-02T12:00:00");
    assertThat(createEventDTOList.get(1).getScheduledStartTime()).isEqualTo("2024-06-09T12:00:00");
    assertThat(createEventDTOList.get(2).getScheduledStartTime()).isEqualTo("2024-06-16T12:00:00");
    assertThat(createEventDTOList.get(3).getScheduledStartTime()).isEqualTo("2024-06-23T12:00:00");
    assertThat(createEventDTOList.get(4).getScheduledStartTime()).isEqualTo("2024-06-30T12:00:00");
  }


  @Test
  public void createWeeklyEventForEuropeInSummerTime() {
    // initiate mock's behaviour
    List<Event> events = Collections.singletonList(sundayEvent(EUROPE_PARIS_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(dispatcher.postRequest(anyString(), any(DiscordEventDTO.class))).thenReturn(createDiscordEventDTO());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_SUMMER_TIME);
    createEventWeeklyHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture());
    var createEventDTOList = argumentCaptor.getAllValues();

    // -2 hours offset
    assertThat(createEventDTOList.get(0).getScheduledStartTime()).isEqualTo("2024-06-02T19:00:00");
    assertThat(createEventDTOList.get(1).getScheduledStartTime()).isEqualTo("2024-06-09T19:00:00");
    assertThat(createEventDTOList.get(2).getScheduledStartTime()).isEqualTo("2024-06-16T19:00:00");
    assertThat(createEventDTOList.get(3).getScheduledStartTime()).isEqualTo("2024-06-23T19:00:00");
    assertThat(createEventDTOList.get(4).getScheduledStartTime()).isEqualTo("2024-06-30T19:00:00");
  }

  @Test
  public void createWeeklyEventWhenFirstDayIsSunday() {
    // initiate mock's behaviour
    List<Event> events = Collections.singletonList(sundayEvent(EUROPE_PARIS_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(dispatcher.postRequest(anyString(), any(DiscordEventDTO.class))).thenReturn(createDiscordEventDTO());

    // on 1.9.2024, it is the first day of the month and is sunday
    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_SUNDAY);
    createEventWeeklyHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture());
    var createEventDTOList = argumentCaptor.getAllValues();

    // The date should be 1.9.2024 as this day is sunday
    assertThat(createEventDTOList.get(0).getScheduledStartTime()).isEqualTo("2024-09-01T19:00:00");
    assertThat(createEventDTOList.get(1).getScheduledStartTime()).isEqualTo("2024-09-08T19:00:00");
    assertThat(createEventDTOList.get(2).getScheduledStartTime()).isEqualTo("2024-09-15T19:00:00");
    assertThat(createEventDTOList.get(3).getScheduledStartTime()).isEqualTo("2024-09-22T19:00:00");
    assertThat(createEventDTOList.get(4).getScheduledStartTime()).isEqualTo("2024-09-29T19:00:00");
  }

  private CreateEventVO createEventVO(LocalDate firstDayOfMonth) {
    return CreateEventVO.builder()
      .firstDayOfMonth(firstDayOfMonth)
      .build();
  }

  private Event sundayEvent(String timezone) {
    return Event.builder()
      .guildId(GUILD_ID)
      .channelId(CHANNEL_ID)
      .name(NAME)
      .description(DESCRIPTION)
      .timeZone(timezone)
      .frequency(Frequency.WEEKLY)
      .dayOfWeek(DayOfWeek.SUNDAY)
      .startTime(START_TIME)
      .build();
  }

  private DiscordEventDTO createDiscordEventDTO() {
    return DiscordEventDTO.builder().build();
  }

}