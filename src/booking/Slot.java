package booking;

import java.time.Duration;
import java.time.LocalDateTime;

public record Slot(LocalDateTime start, LocalDateTime end, Duration duration, LocalDateTime workDayStart, LocalDateTime workDayEnd) {

    @Override
    public String toString() {
        return String.format("[%s] Свободно: %s — %s (%d мин)",
            start.getDayOfWeek().toString(), start.toLocalTime(), end.toLocalTime(), duration.toMinutes());
    }
}
