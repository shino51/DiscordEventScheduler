package org.shino;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateEventDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = -2487741173712561584L;

  private String channelId;
  private String name;
  private String description;
  private LocalDate scheduledStartTime;
  private LocalDate scheduledEndTime;
  private int entityType = 2;
  private int privacyLevel = 2;
}
