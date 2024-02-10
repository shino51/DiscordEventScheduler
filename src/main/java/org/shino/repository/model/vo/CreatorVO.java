package org.shino.repository.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class CreatorVO implements Serializable {

  private String id;
  private String username;
  private String avator;
  private String discriminator;
  private int public_flags;
  private int premium_type;
  private int flags;
  private String banner;
  private int accent_color;
  private String global_name;
  private String avatar_decoration_data;
  private String banner_color;
}
