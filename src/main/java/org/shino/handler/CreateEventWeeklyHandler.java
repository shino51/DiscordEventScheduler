package org.shino.handler;

import org.shino.vo.CreateEventVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreateEventWeeklyHandler {


  @Value("${discord.api.url}")
  private String discordApiUrl;

  public String run(CreateEventVO eventVO) {
    return "";
  }
}
