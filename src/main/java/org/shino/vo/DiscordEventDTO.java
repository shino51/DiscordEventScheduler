package org.shino.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DiscordEventDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = -7273298766068248007L;

  private String id;
  private String guildId;
  private String name;
  private String description;
  private String channelId;
  private String creatorId;
  private CreatorVO creator;
  private String image;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date scheduledStartTime;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date scheduledEndTime;
  private int status;
  private int entityType = 2;
  private transient Object recurrenceRule;
  private int privacyLevel = 2;
  private List<Integer> skuIds;
  private List<String> guildScheduledEventExceptions;
  private transient Object entityMetadata;
}
