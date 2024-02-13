package org.shino.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "EVENT")
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "GUILD_ID")
  private String guildId;

  @Column(name = "CHANNEL_ID")
  private String channelId;

  @Column(name = "FREQUENCY")
  @Enumerated(EnumType.STRING)
  private Frequency frequency;

  @Column(name = "DAY_OF_WEEK")
  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;

  @Column(name = "START_TIME")
  private Integer startTime;

  @Column(name = "TIME_ZONE")
  private String timeZone;

  @Column(name = "DESCRIPTION")
  private String description;
}
