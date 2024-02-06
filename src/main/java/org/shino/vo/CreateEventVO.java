package org.shino.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@Data
public class CreateEventVO implements Serializable {

  @Serial
  private static final long serialVersionUID = 4527620175417116352L;

  private String authKey;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date firstDayOfMonth;
}
