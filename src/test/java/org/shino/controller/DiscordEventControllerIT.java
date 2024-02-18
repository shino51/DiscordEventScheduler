package org.shino.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.shino.service.DiscordEventService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class DiscordEventControllerIT {

  private MockMvc mockMvc;

  @InjectMocks
  private DiscordEventController controller;

  @Mock
  private DiscordEventService service;

  @Before
  public void setUp() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
  }

  @Test
  public void testGetListOfScheduledEvents() throws Exception {
    mockMvc.perform(
        get("/scheduled-event/list")
          .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk());
  }

  @Test
  public void testCreateWeeklyEvent() throws Exception {
    mockMvc.perform(
        post("/scheduled-event/create")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"first_day_of_month\": \"2024-03-01\"}")
      )
      .andExpect(status().isOk());
  }

  @Test
  public void testDeleteEventByDate() throws Exception {
    mockMvc.perform(
        delete("/scheduled-event")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{\"time_zone\": \"ECT\",\"cancelled_date\": \"2024-02-19\"}")
      )
      .andExpect(status().isOk());
  }
}