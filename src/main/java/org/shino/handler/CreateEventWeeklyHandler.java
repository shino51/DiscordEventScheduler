package org.shino.handler;

import lombok.RequiredArgsConstructor;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
import org.shino.model.Event;
import org.shino.model.Frequency;
import org.shino.model.dto.CreateEventDTO;
import org.shino.model.dto.DiscordEventDTO;
import org.shino.repository.EventRepository;
import org.shino.model.vo.CreateEventVO;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.next;

@Component
@RequiredArgsConstructor
public class CreateEventWeeklyHandler {

  private final EventRepository repository;
  private final DiscordEventDispatcher dispatcher;

  public List<DiscordEventDTO> run(CreateEventVO eventVO) {

    List<DiscordEventDTO> output = new ArrayList<>();
    String tailedUrl = "scheduled-events";

    List<CreateEventDTO> dtoList = convertToDTO(eventVO);
    for (CreateEventDTO dto: dtoList) {
      output.add(dispatcher.postRequest(tailedUrl, dto));
    }
    return output;
  }

  private List<CreateEventDTO> convertToDTO(CreateEventVO vo) {
    List<CreateEventDTO> list = new ArrayList<>();

    List<Event> events = repository.findByFrequency(Frequency.EVERY_SUNDAY);
    // get how many weeks in this month
    LocalDate firstDayOfMonth = vo.getFirstDayOfMonth();

    for (Event event: events) {
      LocalDate day = firstDayOfMonth.with(next(SUNDAY));

      LocalDateTime jpnDateTime = day.atTime(event.getStartTime(), 0);

      ZonedDateTime zonedDateTime = ZonedDateTime.of(jpnDateTime, ZoneId.of(event.getTimeZone(), ZoneId.SHORT_IDS));
      LocalDateTime utcDateTime = LocalDateTime.ofInstant(zonedDateTime.toInstant(), ZoneOffset.UTC);

      CreateEventDTO dto = CreateEventDTO.builder()
        .channelId(event.getChannelId())
        .name(event.getName())
        .description(event.getDescription())
        .scheduledStartTime(utcDateTime)
        .entityType(2)
        .privacyLevel(2)
        .build();
      list.add(dto);
      return list;
    }
    return list;
  }
}
