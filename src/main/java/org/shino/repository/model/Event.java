package org.shino.repository.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EVENT")
public class Event {

  @Id
  @GeneratedValue
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

  @Column(name = "START_TIME")
  private Integer startTime;

  @Column(name = "TIME_ZONE")
  private String timeZone;

  @Column(name = "DESCRIPTION")
  private String description;
}
