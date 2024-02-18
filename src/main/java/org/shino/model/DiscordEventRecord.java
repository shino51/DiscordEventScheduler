package org.shino.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiscordEventRecord(
  String id,
  String guildId,
  String name,
  String description,
  String channelId,

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  ZonedDateTime scheduledStartTime,

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
  ZonedDateTime scheduledEndTime,
  int status,
  List<String> guildScheduledEventExceptions,
  int entityType,
  int privacyLevel

) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiscordEventRecord that = (DiscordEventRecord) o;
    return channelId.equals(that.channelId) && scheduledStartTime.equals(that.scheduledStartTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId, scheduledStartTime);
  }
}
