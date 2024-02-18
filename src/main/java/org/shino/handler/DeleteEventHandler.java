package org.shino.handler;

import lombok.RequiredArgsConstructor;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
import org.shino.model.DiscordEventRecord;
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

    List<DiscordEventRecord> existingEvents = getScheduledEventsHandler.run();
    existingEvents.sort(Comparator.comparing(DiscordEventRecord::scheduledStartTime));

    List<DiscordEventRecord> eventsToDelete = getEventsToDelete(eventVO, existingEvents);

    int count = 0;
    outputBuilder.append("The event to be deleted on [")
      .append(eventVO.cancelledDate())
      .append("]\n");
    for (DiscordEventRecord dto : eventsToDelete) {
      String tailedUrl = baseUrl + dto.id();
      dispatcher.deleteRequest(tailedUrl);
      outputBuilder.append(dto.name()).append("\n");
      count++;
    }
    outputBuilder.append("Total deleted events: ").append(count);
    return outputBuilder.toString();
  }

  private List<DiscordEventRecord> getEventsToDelete(DeleteEventVO vo, List<DiscordEventRecord> existingEvents) {
    List<DiscordEventRecord> list = new ArrayList<>();
    ZonedDateTime startDateTime = ZonedDateTime.of(vo.cancelledDate(), LocalTime.MIN, ZoneId.of(vo.timeZone(), ZoneId.SHORT_IDS));
    ZonedDateTime endDateTime = ZonedDateTime.of(vo.cancelledDate(), LocalTime.MAX, ZoneId.of(vo.timeZone(), ZoneId.SHORT_IDS));

    for (DiscordEventRecord dto : existingEvents) {
      if (dto.scheduledStartTime().isBefore(startDateTime)) {
        continue;
      }
      if (dto.scheduledStartTime().isAfter(endDateTime)) {
        return list;
      }
      list.add(dto);
    }
    return list;
  }
}
