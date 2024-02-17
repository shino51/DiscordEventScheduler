package org.shino.model;

import lombok.Getter;

@Getter
public enum Frequency {

  WEEKLY(null),
  MONTHLY_EVERY_FIRST(0),
  MONTHLY_EVERY_SECOND(1),
  MONTHLY_EVERY_THIRD(2),
  MONTHLY_EVERY_FOURTH(3);

  private final Integer weekInMonthOffset;

  Frequency(Integer weekInMonthOffset) {
    this.weekInMonthOffset = weekInMonthOffset;
  }
}
