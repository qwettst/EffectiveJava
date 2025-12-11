import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import booking.BookingSchedule;
import booking.Client;

public class Task2 {

    void main() {
        BookingSchedule dentist = BookingSchedule.builder()
                                                 .workingHours(10, 0, 19, 0)
                                                 .slotDurationMinutes(40)
                                                 .onlyWeekdays()
                                                 .build();

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime slot = LocalDateTime.of(tomorrow, LocalTime.of(11, 20));

        // Записываемся
        boolean booked = dentist.book(slot, new Client("Иванов", "123-321-12"));
        System.out.println("Запись успешна: " + booked);

        // Выводим свободные слоты на неделю
        dentist.freeSlotsBetween(LocalDate.now(), LocalDate.now().plusDays(1)).forEach(System.out::println);
    }
}
