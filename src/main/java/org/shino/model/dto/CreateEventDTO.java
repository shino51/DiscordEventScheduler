package org.shino.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.shino.model.adapter.DescriptionSerializer;
import org.shino.model.adapter.LocalDateTimeTypeAdapter;
import org.shino.model.adapter.LocalDateTimeTypeSerializer;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateEventDTO extends EventDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = -2487741173712561584L;

  private String channelId;
  private String name;

  @JsonSerialize(using = DescriptionSerializer.class)
  private String description;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @JsonSerialize(using = LocalDateTimeTypeSerializer.class)
//  @JsonAdapter(LocalDateTimeTypeAdapter.class)
  private LocalDateTime scheduledStartTime;

  private int entityType;
  private int privacyLevel;
}