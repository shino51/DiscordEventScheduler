package org.shino.handler;

import lombok.RequiredArgsConstructor;
import org.shino.handler.dispatcher.DiscordEventDispatcher;
import org.shino.model.dto.DiscordEventDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetScheduledEventsHandler {

  private final DiscordEventDispatcher eventDispatcher;

  private static final String TAILED_URL = "scheduled-events";

  public List<DiscordEventDTO> run() {
    return eventDispatcher.getRequest(TAILED_URL);
  }
}
