package org.shino.service;

import lombok.RequiredArgsConstructor;
import org.shino.exception.DiscordEventDispatcherException;
import org.shino.handler.CreateEventHandler;
import org.shino.handler.DeleteEventHandler;
import org.shino.handler.GetScheduledEventsHandler;
import org.shino.model.DiscordEventRecord;
import org.shino.model.vo.CreateEventVO;
import org.shino.model.vo.DeleteEventVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscordEventService {

  private final CreateEventHandler createEventHandler;
  private final GetScheduledEventsHandler getScheduledEventsHandler;
  private final DeleteEventHandler deleteEventHandler;

  public List<DiscordEventRecord> getListOfScheduledEvents() {
    return getScheduledEventsHandler.run();
  }

  public List<DiscordEventRecord> createWeeklyEvent(CreateEventVO createEventVO) throws DiscordEventDispatcherException {
    return createEventHandler.run(createEventVO);
  }

  public String deleteEvent(DeleteEventVO eventVO) {
    return deleteEventHandler.run(eventVO);
  }
}
