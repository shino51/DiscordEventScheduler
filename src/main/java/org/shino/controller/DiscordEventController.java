package org.shino.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.shino.exception.DiscordEventDispatcherException;
import org.shino.model.DiscordEventRecord;
import org.shino.model.vo.CreateEventVO;
import org.shino.model.vo.DeleteEventVO;
import org.shino.service.DiscordEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scheduled-event")
@RequiredArgsConstructor
public class DiscordEventController {

  private final DiscordEventService service;

  @Value("${discord.api.url}")
  private String discordApiUrl;

  @GetMapping("/list")
  @ResponseBody
  public ResponseEntity<List<DiscordEventRecord>> getListOfScheduledEvents() {
    List<DiscordEventRecord> result = service.getListOfScheduledEvents();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/create")
  @ResponseBody
  public ResponseEntity<List<DiscordEventRecord>> createWeeklyEvent(@RequestBody CreateEventVO createEventVO) throws DiscordEventDispatcherException {
    // call discord API to create a event
    List<DiscordEventRecord> result = service.createWeeklyEvent(createEventVO);
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
