package org.shino.handler;

import lombok.RequiredArgsConstructor;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
import org.shino.model.Event;
import org.shino.model.Frequency;
import org.shino.model.dto.DiscordEventDTO;
import org.shino.model.vo.CreateEventVO;
import org.shino.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.next;

@Component
@RequiredArgsConstructor
public class CreateEventHandler {

  private final EventRepository repository;
  private final DiscordEventDispatcher dispatcher;
  private final GetScheduledEventsHandler getScheduledEventsHandler;

  public List<DiscordEventDTO> run(CreateEventVO eventVO) {

    List<DiscordEventDTO> output = new ArrayList<>();
    String tailedUrl = "scheduled-events";

    List<DiscordEventDTO> existingEvents = getScheduledEventsHandler.run();
    List<DiscordEventDTO> dtoList = createEventDTOs(eventVO, existingEvents);
    for (DiscordEventDTO dto : dtoList) {
      output.add(dispatcher.postRequest(tailedUrl, dto));
    }
    return output;
  }

  private List<DiscordEventDTO> createEventDTOs(CreateEventVO vo, List<DiscordEventDTO> existingEvents) {
    List<DiscordEventDTO> list = new ArrayList<>();

    // get how many weeks in this month
    LocalDate firstDayOfMonth = vo.getFirstDayOfMonth();
    // create event only within the month. This is used to terminate the event creation
    ZonedDateTime firstDayOfNextMonth = getUtcLocalDateTime("UTC", firstDayOfMonth.plusMonths(1), LocalTime.of(0, 0));

    // create weekly event's DTO
    createWeeklyEventsDTO(existingEvents, list, firstDayOfMonth, firstDayOfNextMonth);

    // create monthly event's DTO
    createMonthlyEventsDTO(existingEvents, list, firstDayOfMonth, firstDayOfNextMonth);

    return list;
  }

  private void createWeeklyEventsDTO(List<DiscordEventDTO> existingEvents, List<DiscordEventDTO> list, LocalDate firstDayOfMonth, ZonedDateTime firstDayOfNextMonth) {
    List<Event> events = repository.findByFrequency(Frequency.WEEKLY);
    for (Event event : events) {
      DayOfWeek eventDay = event.getDayOfWeek();
      LocalDate eventDate = firstDayOfMonth.getDayOfWeek().equals(eventDay) ? firstDayOfMonth : firstDayOfMonth.with(next(eventDay));
      LocalTime startTime = LocalTime.of(event.getStartTime(), 0);

      ZonedDateTime utcDateTime = getUtcLocalDateTime(event.getTimeZone(), eventDate, startTime);
      do {
        var dto = createDTOByEvent(event, utcDateTime);
        if (!existingEvents.contains(dto)) {
          list.add(dto);
        }
        // shift to next event date
        eventDate = eventDate.plusWeeks(1);
        utcDateTime = getUtcLocalDateTime(event.getTimeZone(), eventDate, startTime);
      } while (utcDateTime.isBefore(firstDayOfNextMonth));
    }
  }

  private void createMonthlyEventsDTO(List<DiscordEventDTO> existingEvents, List<DiscordEventDTO> list, LocalDate firstDayOfMonth, ZonedDateTime firstDayOfNextMonth) {
    List<Event> events = repository.findByFrequencyIn(
      Frequency.MONTHLY_EVERY_FIRST,
      Frequency.MONTHLY_EVERY_SECOND,
      Frequency.MONTHLY_EVERY_THIRD,
      Frequency.MONTHLY_EVERY_FOURTH
    );

    for (Event event : events) {
      DayOfWeek eventDay = event.getDayOfWeek();
      LocalDate eventDate = firstDayOfMonth.getDayOfWeek().equals(eventDay) ? firstDayOfMonth : firstDayOfMonth.with(next(eventDay));
      eventDate = eventDate.plusWeeks(event.getFrequency().getWeekInMonthOffset());

      LocalTime startTime = LocalTime.of(event.getStartTime(), 0);
      ZonedDateTime utcDateTime = getUtcLocalDateTime(event.getTimeZone(), eventDate, startTime);

      if (utcDateTime.isBefore(firstDayOfNextMonth)) {
        var dto = createDTOByEvent(event, utcDateTime);
        if (!existingEvents.contains(dto)) {
          list.add(dto);
        }
      }

    }
  }

  private ZonedDateTime getUtcLocalDateTime(String timeZoneShortId, LocalDate eventDate, LocalTime startTime) {
    return ZonedDateTime.of(eventDate, startTime, ZoneId.of(timeZoneShortId, ZoneId.SHORT_IDS))
      .withZoneSameInstant(ZoneOffset.UTC);
  }

  private DiscordEventDTO createDTOByEvent(Event event, ZonedDateTime utcDateTime) {
    return DiscordEventDTO.builder()
      .channelId(event.getChannelId())
      .name(event.getName())
      .description(event.getDescription())
      .scheduledStartTime(utcDateTime)
      .entityType(2)
      .privacyLevel(2)
      .build();
  }
}
