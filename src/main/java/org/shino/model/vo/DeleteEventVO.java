package org.shino.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DeleteEventVO(
  String timeZone,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  LocalDate cancelledDate) {
}
