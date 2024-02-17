package org.shino.handler;

import lombok.RequiredArgsConstructor;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
import org.shino.model.dto.DiscordEventDTO;
import org.shino.model.vo.DeleteEventVO;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteEventHandler {

  private final GetScheduledEventsHandler getScheduledEventsHandler;
  private final DiscordEventDispatcher dispatcher;

  public String run(DeleteEventVO eventVO) {
    StringBuilder outputBuilder = new StringBuilder();
    String baseUrl = "scheduled-events/";

    List<DiscordEventDTO> existingEvents = getScheduledEventsHandler.run();
    existingEvents.sort(Comparator.comparing(DiscordEventDTO::getScheduledStartTime));

    List<DiscordEventDTO> eventsToDelete = getEventsToDelete(eventVO, existingEvents);

    int count = 0;
    outputBuilder.append("The event to be deleted on [")
      .append(eventVO.getCancelledDate())
      .append("]\n");
    for (DiscordEventDTO dto : eventsToDelete) {
      String tailedUrl = baseUrl + dto.getId();
      dispatcher.deleteRequest(tailedUrl);
      outputBuilder.append(dto.getName()).append("\n");
      count++;
    }
    outputBuilder.append("Total deleted events: ").append(count);
    return outputBuilder.toString();
  }

  private List<DiscordEventDTO> getEventsToDelete(DeleteEventVO vo, List<DiscordEventDTO> existingEvents) {
    List<DiscordEventDTO> list = new ArrayList<>();
    ZonedDateTime startDateTime = ZonedDateTime.of(vo.getCancelledDate(), LocalTime.MIN, ZoneId.of(vo.getTimeZone(), ZoneId.SHORT_IDS));
    ZonedDateTime endDateTime = ZonedDateTime.of(vo.getCancelledDate(), LocalTime.MAX, ZoneId.of(vo.getTimeZone(), ZoneId.SHORT_IDS));

    for (DiscordEventDTO dto : existingEvents) {
      if (dto.getScheduledStartTime().isBefore(startDateTime)) {
        continue;
      }
      if (dto.getScheduledStartTime().isAfter(endDateTime)) {
        return list;
      }
      list.add(dto);
    }
    return list;
  }
}
