package com.ss.utopia.tickets.bootstrap;

import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.repository.TicketsRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local-h2")
@RequiredArgsConstructor
public class H2DataBootstrap implements CommandLineRunner {


  private final TicketsRepository ticketsRepository;

  @Override
  public void run(String... args) {
    if (ticketsRepository.count() == 0) {
      loadAllTickets();
    }
  }

  private void loadAllTickets() {
    final var mockUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
    final var mockName = "John Sample";
    final var mockZone = ZoneId.of("America/New_York");

    var ticket1 = Ticket.builder()
            .id(1L)
            .flightId(1L)
            .flightTime(ZonedDateTime.of(LocalDateTime.of(2021, 3, 10, 8, 24, 0),
                    mockZone))
            .purchaserId(mockUuid)
            .passengerName(mockName)
            .seatClass("First")
            .seatNumber("1A")
            .status(Ticket.TicketStatus.CHECKED_IN)
            .build();
    ticketsRepository.save(ticket1);

    var ticket2 = Ticket.builder()
            .id(2L)
            .flightId(2L)
            .flightTime(ZonedDateTime.of(LocalDateTime.of(2021, 6, 21, 12, 0, 0),
                    mockZone))
            .purchaserId(mockUuid)
            .passengerName(mockName)
            .seatClass("Executive")
            .seatNumber("1A")
            .status(Ticket.TicketStatus.PURCHASED)
            .build();
    ticketsRepository.save(ticket2);

    var ticket3 = Ticket.builder()
            .id(3L)
            .flightId(3L)
            .flightTime(ZonedDateTime.of(LocalDateTime.of(2021, 1, 1, 12, 0, 0),
                    mockZone))
            .purchaserId(mockUuid)
            .passengerName(mockName)
            .seatClass("Coach")
            .seatNumber("1A")
            .status(Ticket.TicketStatus.CHECKED_IN)
            .build();
    ticketsRepository.save(ticket3);

    var ticket4 = Ticket.builder()
            .id(4L)
            .flightId(4L)
            .flightTime(ZonedDateTime.of(LocalDateTime.of(2021, 3, 24, 10, 0, 0),
                    mockZone))
            .purchaserId(mockUuid)
            .passengerName(mockName)
            .seatClass("Economy")
            .seatNumber("1A")
            .status(Ticket.TicketStatus.CANCELLED)
            .build();
    ticketsRepository.save(ticket4);
  }
}
