package org.shino.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shino.exception.DiscordEventDispatcherException;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
import org.shino.model.Event;
import org.shino.model.Frequency;
import org.shino.model.DiscordEventRecord;
import org.shino.model.vo.CreateEventVO;
import org.shino.repository.EventRepository;

import java.time.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateEventHandlerTest {

  private static final String CHANNEL_ID = "123456";
  private static final String GUILD_ID = "545655";
  private static final Integer DEFAULT_START_TIME = 21;
  private static final Integer SECOND_START_TIME = 21;
  private static final String NAME = "name of the event";
  private static final String DESCRIPTION = "description";

  private static final LocalDate FIRST_DAY_OF_MONTH_IN_WINTER_TIME = LocalDate.of(2024, 3, 1);
  private static final LocalDate FIRST_DAY_OF_MONTH_IN_SUMMER_TIME = LocalDate.of(2024, 6, 1);
  private static final LocalDate FIRST_DAY_OF_MONTH_SUNDAY = LocalDate.of(2024, 9, 1);
  private static final String JAPAN_TIMEZONE = "JST";
  private static final String EUROPE_PARIS_TIMEZONE = "ECT";

  @Captor
  ArgumentCaptor<DiscordEventRecord> argumentCaptor = ArgumentCaptor.forClass(DiscordEventRecord.class);

  @Mock
  private DiscordEventDispatcher dispatcher;

  @Mock
  private EventRepository repository;

  @Mock
  private GetScheduledEventsHandler getScheduledEventsHandler;

  @InjectMocks
  private CreateEventHandler createEventHandler;

  @Before
  public void setUp() throws DiscordEventDispatcherException {
    when(dispatcher.postRequest(anyString(), any(DiscordEventRecord.class), eq(0))).thenReturn(createResponse());
  }

  @Test
  public void testCreateWeeklyEventInJpWinterTime() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Collections.singletonList(createWeeklyEvent(JAPAN_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(Collections.emptyList());
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_WINTER_TIME);
    List<DiscordEventRecord> result = createEventHandler.run(vo);

    // discord event DTO gets created
    assertThat(result).isNotNull().hasSize(5);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture(), eq(0));
    List<DiscordEventRecord> createEventDTOList = argumentCaptor.getAllValues();

    // validate contents of the first dto
    var firstCreatedDTO = createEventDTOList.get(0);
    assertThat(firstCreatedDTO.channelId()).isEqualTo(CHANNEL_ID);
    assertThat(firstCreatedDTO.description()).isEqualTo(DESCRIPTION);
    assertThat(firstCreatedDTO.name()).isEqualTo(NAME);
    // The first sunday in March 2024 is 3.3 and JST 21:00 is UTC 12:00
    assertThat(firstCreatedDTO.scheduledStartTime()).isEqualTo("2024-03-03T12:00Z");

    // validate event dates for the rest of dtos
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-03-10T12:00Z");
    assertThat(createEventDTOList.get(2).scheduledStartTime()).isEqualTo("2024-03-17T12:00Z");
    assertThat(createEventDTOList.get(3).scheduledStartTime()).isEqualTo("2024-03-24T12:00Z");
    assertThat(createEventDTOList.get(4).scheduledStartTime()).isEqualTo("2024-03-31T12:00Z");
  }

  @Test
  public void createWeeklyEventForEuropeInWinterTime() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Collections.singletonList(createWeeklyEvent(EUROPE_PARIS_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(Collections.emptyList());
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_WINTER_TIME);
    createEventHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture(), eq(0));
    var createEventDTOList = argumentCaptor.getAllValues();
    // -1 hours offset
    assertThat(createEventDTOList.get(0).scheduledStartTime()).isEqualTo("2024-03-03T20:00Z");
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-03-10T20:00Z");
    assertThat(createEventDTOList.get(2).scheduledStartTime()).isEqualTo("2024-03-17T20:00Z");
    assertThat(createEventDTOList.get(3).scheduledStartTime()).isEqualTo("2024-03-24T20:00Z");
    // on the last day is the end of winter time. From this day, offset is -2 instead of -1
    assertThat(createEventDTOList.get(4).scheduledStartTime()).isEqualTo("2024-03-31T19:00Z");
  }

  @Test
  public void createWeeklyEventForJapanInSummerTime() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Collections.singletonList(createWeeklyEvent(JAPAN_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(Collections.emptyList());
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_SUMMER_TIME);
    createEventHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture(), eq(0));
    var createEventDTOList = argumentCaptor.getAllValues();
    // +9 hours offset
    assertThat(createEventDTOList.get(0).scheduledStartTime()).isEqualTo("2024-06-02T12:00Z");
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-06-09T12:00Z");
    assertThat(createEventDTOList.get(2).scheduledStartTime()).isEqualTo("2024-06-16T12:00Z");
    assertThat(createEventDTOList.get(3).scheduledStartTime()).isEqualTo("2024-06-23T12:00Z");
    assertThat(createEventDTOList.get(4).scheduledStartTime()).isEqualTo("2024-06-30T12:00Z");
  }

  @Test
  public void createWeeklyEventForEuropeInSummerTime() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Collections.singletonList(createWeeklyEvent(EUROPE_PARIS_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(Collections.emptyList());
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_SUMMER_TIME);
    createEventHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture(), eq(0));
    var createEventDTOList = argumentCaptor.getAllValues();

    // -2 hours offset
    assertThat(createEventDTOList.get(0).scheduledStartTime()).isEqualTo("2024-06-02T19:00Z");
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-06-09T19:00Z");
    assertThat(createEventDTOList.get(2).scheduledStartTime()).isEqualTo("2024-06-16T19:00Z");
    assertThat(createEventDTOList.get(3).scheduledStartTime()).isEqualTo("2024-06-23T19:00Z");
    assertThat(createEventDTOList.get(4).scheduledStartTime()).isEqualTo("2024-06-30T19:00Z");
  }

  @Test
  public void createWeeklyEventWhenFirstDayIsSunday() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Collections.singletonList(createWeeklyEvent(EUROPE_PARIS_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(Collections.emptyList());
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    // on 1.9.2024, it is the first day of the month and is sunday
    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_SUNDAY);
    createEventHandler.run(vo);

    // verify argument passed
    verify(dispatcher, times(5)).postRequest(anyString(), argumentCaptor.capture(), eq(0));
    var createEventDTOList = argumentCaptor.getAllValues();

    // The date should be 1.9.2024 as this day is sunday
    assertThat(createEventDTOList.get(0).scheduledStartTime()).isEqualTo("2024-09-01T19:00Z");
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-09-08T19:00Z");
    assertThat(createEventDTOList.get(2).scheduledStartTime()).isEqualTo("2024-09-15T19:00Z");
    assertThat(createEventDTOList.get(3).scheduledStartTime()).isEqualTo("2024-09-22T19:00Z");
    assertThat(createEventDTOList.get(4).scheduledStartTime()).isEqualTo("2024-09-29T19:00Z");
  }

  @Test
  public void createMultipleEventWithDifferentDayOfWeek() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Arrays.asList(createEvent(EUROPE_PARIS_TIMEZONE, Frequency.WEEKLY, DayOfWeek.SUNDAY, DEFAULT_START_TIME),
      createEvent(EUROPE_PARIS_TIMEZONE, Frequency.WEEKLY, DayOfWeek.FRIDAY, DEFAULT_START_TIME),
      createEvent(EUROPE_PARIS_TIMEZONE, Frequency.WEEKLY, DayOfWeek.SUNDAY, SECOND_START_TIME));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(Collections.emptyList());
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    // on 1.9.2024, it is the first day of the month and is sunday
    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_SUNDAY);
    createEventHandler.run(vo);

    // 15 events should get created
    verify(dispatcher, times(14)).postRequest(anyString(), any(DiscordEventRecord.class), eq(0));
  }

  @Test
  public void shouldNotSendDuplicatedEventRequest() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Collections.singletonList(createWeeklyEvent(JAPAN_TIMEZONE));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(events);
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(Collections.emptyList());

    // duplicated event should be found
    when(getScheduledEventsHandler.run())
      .thenReturn(Collections.singletonList(
          createResponse(
            getUtcLocalDateTime(LocalDate.of(2024, 3, 3), LocalTime.of(12, 0)))
        )
      );

    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_WINTER_TIME);
    createEventHandler.run(vo);

    // only 4 events should get created
    verify(dispatcher, times(4)).postRequest(anyString(), argumentCaptor.capture(), eq(0));

    var createEventDTOList = argumentCaptor.getAllValues();

    // validate event dates for the rest of dtos
    // The create with the date of UTC 2024.3.3 12:00 is eliminated
    assertThat(createEventDTOList.get(0).scheduledStartTime()).isEqualTo("2024-03-10T12:00Z");
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-03-17T12:00Z");
    assertThat(createEventDTOList.get(2).scheduledStartTime()).isEqualTo("2024-03-24T12:00Z");
    assertThat(createEventDTOList.get(3).scheduledStartTime()).isEqualTo("2024-03-31T12:00Z");
  }

  @Test
  public void createMonthlyEvent() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Arrays.asList(createMonthlyEvent(Frequency.MONTHLY_EVERY_FIRST), createMonthlyEvent(Frequency.MONTHLY_EVERY_FOURTH));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(Collections.emptyList());
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(events);
    // duplicated event should be found
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    // on 1.9.2024, it is the first day of the month and is sunday
    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_IN_WINTER_TIME);
    createEventHandler.run(vo);

    // only 4 events should get created
    verify(dispatcher, times(2)).postRequest(anyString(), argumentCaptor.capture(), eq(0));

    var createEventDTOList = argumentCaptor.getAllValues();

    // validate event dates
    assertThat(createEventDTOList.get(0).scheduledStartTime()).isEqualTo("2024-03-03T12:00Z");
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-03-24T12:00Z");
  }

  @Test
  public void createMonthlyEventWhenFirstDayIsThatDay() throws DiscordEventDispatcherException {
    // define mock's behaviour
    List<Event> events = Arrays.asList(createMonthlyEvent(Frequency.MONTHLY_EVERY_FIRST), createMonthlyEvent(Frequency.MONTHLY_EVERY_FOURTH));
    when(repository.findByFrequency(Frequency.WEEKLY)).thenReturn(Collections.emptyList());
    when(repository.findByFrequencyIn(any(Frequency[].class))).thenReturn(events);
    // duplicated event should be found
    when(getScheduledEventsHandler.run()).thenReturn(Collections.emptyList());

    // on 1.9.2024, it is the first day of the month and is sunday
    CreateEventVO vo = createEventVO(FIRST_DAY_OF_MONTH_SUNDAY);
    createEventHandler.run(vo);

    // only 4 events should get created
    verify(dispatcher, times(2)).postRequest(anyString(), argumentCaptor.capture(), eq(0));

    var createEventDTOList = argumentCaptor.getAllValues();

    // validate event dates
    assertThat(createEventDTOList.get(0).scheduledStartTime()).isEqualTo("2024-09-01T12:00Z");
    assertThat(createEventDTOList.get(1).scheduledStartTime()).isEqualTo("2024-09-22T12:00Z");
  }

  private CreateEventVO createEventVO(LocalDate firstDayOfMonth) {
    return CreateEventVO.builder()
      .firstDayOfMonth(firstDayOfMonth)
      .build();
  }

  private Event createWeeklyEvent(String timezone) {
    return createEvent(timezone, Frequency.WEEKLY, DayOfWeek.SUNDAY, DEFAULT_START_TIME);
  }

  private Event createMonthlyEvent(Frequency frequency) {
    return createEvent(JAPAN_TIMEZONE, frequency, DayOfWeek.SUNDAY, DEFAULT_START_TIME);
  }

  private Event createEvent(String timezone, Frequency frequency, DayOfWeek dayOfWeek, Integer startTime) {
    return Event.builder()
      .guildId(GUILD_ID)
      .channelId(CHANNEL_ID)
      .name(NAME)
      .description(DESCRIPTION)
      .timeZone(timezone)
      .frequency(frequency)
      .dayOfWeek(dayOfWeek)
      .startTime(startTime)
      .build();
  }

  private DiscordEventRecord createResponse() {
    return createResponse(
      getUtcLocalDateTime(LocalDate.of(2024, 3, 3), LocalTime.of(12, 0))
    );
  }

  private DiscordEventRecord createResponse(ZonedDateTime startTime) {
    return DiscordEventRecord.builder()
      .channelId(CHANNEL_ID)
      .name(NAME)
      .description(DESCRIPTION)
      .scheduledStartTime(startTime)
      .build();
  }

  private ZonedDateTime getUtcLocalDateTime(LocalDate eventDate, LocalTime startTime) {
    return ZonedDateTime.of(eventDate, startTime, ZoneId.of("UTC", ZoneId.SHORT_IDS))
      .withZoneSameInstant(ZoneOffset.UTC);
  }
}