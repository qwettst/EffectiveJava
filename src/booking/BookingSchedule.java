package booking;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.temporal.ChronoUnit.MINUTES;

public class BookingSchedule {

    private final LocalTime workDayStart;
    private final LocalTime workDayEnd;
    private final Duration slotDuration;
    private final Set<DayOfWeek> workingDays;

    private final Map<LocalDateTime, Booking> bookings = new HashMap<>();

    private BookingSchedule(Builder builder) {
        this.workDayStart = Objects.requireNonNull(builder.start);
        this.workDayEnd = Objects.requireNonNull(builder.end);
        this.slotDuration = Objects.requireNonNull(builder.duration).truncatedTo(MINUTES);
        this.workingDays = Set.copyOf(builder.workingDays);

        if (!workDayStart.isBefore(workDayEnd)) {
            throw new IllegalArgumentException("Начало рабочего дня должно быть раньше конца");
        }
        if (slotDuration.isZero() || slotDuration.isNegative()) {
            throw new IllegalArgumentException("Продолжительность слота должна быть положительной");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Stream<Slot> freeSlotsBetween(LocalDate ds, LocalDate de) {
        return allSlotsBetween(ds, de)
                   .filter(slot -> !bookings.containsKey(slot.start()));
    }

    public Stream<Slot> allSlotsBetween(LocalDate ds, LocalDate de) {
        if (ds.isAfter(de)) {
            return Stream.empty();
        }

        return ds.datesUntil(de.plusDays(1))
                   .filter(this::isWorkingDay)
                   .flatMap(this::slotsOnDay);
    }

    public boolean book(LocalDateTime slotStart, Client client) {
        Objects.requireNonNull(slotStart);
        Objects.requireNonNull(client);

        //TODO: проверка на запись

        Booking booking = new Booking(
            slotStart,
            client,
            LocalDateTime.now()
        );

        return bookings.putIfAbsent(slotStart, booking) == null;
    }

    public boolean cancel(LocalDateTime slotStart) {
        return bookings.remove(slotStart) != null;
    }

    public Booking getBooking(LocalDateTime slotStart) {
        return bookings.get(slotStart);
    }

    private boolean isWorkingDay(LocalDate date) {
        return workingDays.contains(date.getDayOfWeek());
    }

    private Stream<Slot> slotsOnDay(LocalDate date) {
        LocalDateTime ds = LocalDateTime.of(date, workDayStart);
        LocalDateTime de = LocalDateTime.of(date, workDayEnd);

        List<Slot> slots = new ArrayList<>();
        LocalDateTime current = ds;

        while (!current.plus(slotDuration).isAfter(de)) {
            LocalDateTime slotEnd = current.plus(slotDuration);

            slots.add(new Slot(
                current,
                slotEnd,
                slotDuration,
                ds,
                de
            ));

            current = slotEnd;
        }

        return slots.stream();
    }

    public static final class Builder {

        private LocalTime start = LocalTime.of(9, 0);
        private LocalTime end = LocalTime.of(18, 0);
        private Duration duration = Duration.ofMinutes(30);
        private final Set<DayOfWeek> workingDays = EnumSet.allOf(DayOfWeek.class);

        public Builder workingHours(int startHour, int startMinute, int endHour, int endMinute) {
            this.start = LocalTime.of(startHour, startMinute);
            this.end = LocalTime.of(endHour, endMinute);
            return this;
        }

        public Builder workingHours(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
            return this;
        }

        public Builder slotDurationMinutes(int minutes) {
            this.duration = Duration.ofMinutes(minutes);
            return this;
        }

        public Builder onlyWeekdays() {
            workingDays.clear();
            workingDays.addAll(Set.of(DayOfWeek.MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY));
            return this;
        }

        public Builder workingDays(DayOfWeek... days) {
            workingDays.clear();
            workingDays.addAll(Arrays.asList(days));
            return this;
        }

        public BookingSchedule build() {
            return new BookingSchedule(this);
        }
    }
}
