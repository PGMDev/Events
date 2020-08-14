package dev.pgm.events.ready;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;

public class ReadySystem {

  int cancelCounter = 0;
  private Timestamp timestamp;
  private Duration countdownLength;
  private boolean runningSmallerCountdown = false;

  public void reset() {
    timestamp = null;
    countdownLength = null;
    runningSmallerCountdown = false;
  }

  public boolean canReadyAction() {
    if (timestamp == null || countdownLength == null) return true;

    return remaining().compareTo(Duration.ofSeconds(21)) > 0;
  }

  public boolean canUnready() {
    if (timestamp == null || countdownLength == null) return true;

    return remaining().compareTo(Duration.ofSeconds(21)) > 0;
  }

  public void onStart(Duration countdownDuration, boolean allReady) {
    if (allReady && countdownDuration.compareTo(Duration.ofSeconds(21)) < 1) {
      // if the above check passes it's probably an autoready-start countdown
      runningSmallerCountdown = true;
      return;
    }

    this.timestamp = Timestamp.from(Instant.now());
    this.countdownLength = countdownDuration;
    runningSmallerCountdown = false;
    cancelCounter = 0;
  }

  public boolean unreadyShouldCancel() {
    // whether an unready (when teams are all ready) should result in a cancel
    return runningSmallerCountdown;
  }

  public @Nullable Duration onCancel(boolean allReady) {
    if (allReady && cancelCounter == 1) {
      // all are ready and still cancel means a manual cancel -- lets just stop everything
      // counter == 1 means real cancel
      runningSmallerCountdown = false;
      timestamp = null;
      countdownLength = null;
      cancelCounter = 0;
      return null;
    }

    if (allReady && cancelCounter == 0) {
      cancelCounter = 1;
      return null;
    }

    if (runningSmallerCountdown && timestamp != null) {
      Duration remaining = remaining();

      if (remaining.compareTo(Duration.ZERO) < 1) {
        // if less than 5 seconds remaining cancel this
        return Duration.ZERO;
      }

      return remaining;
    }

    return null;
  }

  private Duration remaining() {
    long diff = Timestamp.from(Instant.now()).getTime() - timestamp.getTime();
    return countdownLength.minus(Duration.ofMillis(diff));
  }
}
