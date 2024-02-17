package org.shino.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shino.model.dto.DiscordEventDTO;
import org.shino.model.vo.CreateEventVO;
import org.shino.model.vo.DeleteEventVO;
import org.shino.service.DiscordEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class DiscordEventController {

  private final DiscordEventService service;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @GetMapping("/list/events")
  @ResponseBody
  public ResponseEntity<List<DiscordEventDTO>> getListOfScheduledEvents() {
    List<DiscordEventDTO> result = service.getListOfScheduledEvents();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/create/weekly")
  @ResponseBody
  public ResponseEntity<List<DiscordEventDTO>> createWeeklyEvent(@RequestBody CreateEventVO createEventVO) {
    // call discord API to create a event
    List<DiscordEventDTO> result = service.createWeeklyEvent(createEventVO);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @DeleteMapping
  @ResponseBody
  public ResponseEntity<String> deleteEventByDate(@RequestBody DeleteEventVO eventVO) {
    // call discord API to create a event
    String result = service.deleteEvent(eventVO);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> exceptionHandler(HttpServletRequest req, Exception ex) {
    String message = "Request: " + req.getRequestURL() + " throws following exception: \n" + ex.getMessage();
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }
}
