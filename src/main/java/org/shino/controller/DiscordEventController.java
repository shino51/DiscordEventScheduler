package org.shino.controller;

import org.shino.service.DiscordEventService;
import org.shino.vo.CreateEventVO;
import org.shino.vo.DiscordEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
public class DiscordEventController {

  @Autowired
  private DiscordEventService service;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @GetMapping("/version")
  public String getVersion() {
    return "version 1, API URL: " + discordApiUrl;
  }

  @GetMapping("/list/events")
  @ResponseBody
  public ResponseEntity<List<DiscordEventDTO>> getListOfScheduledEvents() {
    List<DiscordEventDTO> result = service.getListOfScheduledEvents();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/create/weekly")
  @ResponseBody
  public ResponseEntity<String> createWeeklyEvent(@RequestBody CreateEventVO createEventVO) {
    // call discord API to create a event
    String result = service.createWeeklyEvent(createEventVO);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
