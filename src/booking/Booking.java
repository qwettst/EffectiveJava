package booking;

import java.time.LocalDateTime;

public record Booking(LocalDateTime slotStart, Client client, LocalDateTime bookedAt) {

    @Override
    public String toString() {
        return String.format("%s — %s (тел: %s, записан: %s)",
            slotStart.toLocalDate(),
            slotStart.toLocalTime(),
            client.phone(),
            bookedAt.toLocalTime());
    }
}
