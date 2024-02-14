package org.shino.model;

import lombok.Getter;

@Getter
public enum Frequency {

  WEEKLY(null),
  MONTHLY_EVERY_SECOND(2),
  MONTHLY_EVERY_THIRD(3),
  MONTHLY_EVERY_FOURTH(4);

  private final Integer weekInMonth;

  Frequency(Integer weekInMonth) {
    this.weekInMonth = weekInMonth;
  }
}
