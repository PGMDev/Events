package dev.pgm.events.ready;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;

public class ReadySystem {

  private Timestamp timestamp;
  private Duration countdownLength;
  private boolean runningSmallerCountdown = false;

  public void reset() {
    timestamp = null;
    countdownLength = null;
    runningSmallerCountdown = false;
  }

  public boolean canReady() {
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
  }

  public boolean unreadyShouldCancel() {
    // whether an unready (when teams are all ready) should result in a cancel
    return runningSmallerCountdown;
  }

  public @Nullable Duration getResetDuration() {
    if (runningSmallerCountdown && timestamp != null) {
      Duration remaining = remaining();
      runningSmallerCountdown = false;

      return remaining;
    }

    return null;
  }

  private Duration remaining() {
    long diff = Timestamp.from(Instant.now()).getTime() - timestamp.getTime();
    return countdownLength.minus(Duration.ofMillis(diff));
  }
}
