package org.shino.service;

import lombok.RequiredArgsConstructor;
import org.shino.handler.CreateEventWeeklyHandler;
import org.shino.handler.GetScheduledEventsHandler;
import org.shino.model.dto.DiscordEventDTO;
import org.shino.model.vo.CreateEventVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscordEventService {

  private final CreateEventWeeklyHandler createEventWeeklyHandler;
  private final GetScheduledEventsHandler getScheduledEventsHandler;

  public List<DiscordEventDTO> getListOfScheduledEvents() {
    return getScheduledEventsHandler.run();
  }

  public List<DiscordEventDTO> createWeeklyEvent(CreateEventVO createEventVO) {
    return createEventWeeklyHandler.run(createEventVO);
  }
}
