package org.shino.exception;

import java.io.Serial;

public class DiscordEventDispatcherException extends Exception {

  @Serial
  private static final long serialVersionUID = 9103224908341303099L;

  public DiscordEventDispatcherException(String message, Throwable cause) {
    super(message, cause);
  }
}
