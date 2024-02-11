package org.shino.service;

import org.shino.handler.CreateEventWeeklyHandler;
import org.shino.handler.GetScheduledEventsHandler;
import org.shino.model.vo.CreateEventVO;
import org.shino.model.dto.DiscordEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscordEventService {

  @Autowired
  private CreateEventWeeklyHandler createEventWeeklyHandler;

  @Autowired
  private GetScheduledEventsHandler getScheduledEventsHandler;

  public List<DiscordEventDTO> getListOfScheduledEvents() {
    return getScheduledEventsHandler.run();
  }

  public List<DiscordEventDTO> createWeeklyEvent(CreateEventVO createEventVO) {
    return createEventWeeklyHandler.run(createEventVO);
  }
}
