package org.shino.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Builder
@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DiscordEventDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = -7273298766068248007L;

  private String id;
  private String guildId;
  private String name;
  private String description;
  private String channelId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime scheduledStartTime;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime scheduledEndTime;
  private int status;
  private List<String> guildScheduledEventExceptions;
  private int entityType;
  private int privacyLevel;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiscordEventDTO that = (DiscordEventDTO) o;
    return channelId.equals(that.channelId) && scheduledStartTime.equals(that.scheduledStartTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId, scheduledStartTime);
  }
}
