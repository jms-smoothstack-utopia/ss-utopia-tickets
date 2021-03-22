package com.ss.utopia.tickets.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.dto.TicketItem;
import com.ss.utopia.tickets.exception.BadStatusUpdateException;
import com.ss.utopia.tickets.exception.NoSuchTicketException;
import com.ss.utopia.tickets.repository.TicketsRepository;
import com.ss.utopia.tickets.entity.Ticket;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.time.ZonedDateTime;

import com.ss.utopia.tickets.service.TicketService;
import com.ss.utopia.tickets.service.TicketsServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

class TicketsServiceImplUnitTest {

    private static Ticket firstTicket;
    private static Ticket pastTicket;
    private static Ticket futureTicket;
    private static Ticket cancelledTicket;
    private final static UUID customerId = UUID.randomUUID();
    private final static ZonedDateTime mockTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
    private final static ZonedDateTime mockPast = mockTime.minus(6, ChronoUnit.MONTHS);
    private final static ZonedDateTime mockFuture = mockTime.plus(6, ChronoUnit.MONTHS);
    private final TicketsRepository repository = Mockito.mock(TicketsRepository.class);
    private final TicketService service = new TicketsServiceImpl(repository);

    @BeforeAll
    static void beforeAll() {
        firstTicket = Ticket.builder()
                .id(1L)
                .flightId(1L)
                .flightTime(mockTime)
                .purchaserId(customerId)
                .passengerName("Test Customer")
                .seatClass("Executive")
                .seatNumber("1A")
                .status(Ticket.TicketStatus.CHECKED_IN)
                .build();
        pastTicket = Ticket.builder()
                .id(2L)
                .flightId(2L)
                .flightTime(mockPast)
                .purchaserId(customerId)
                .passengerName("Test Customer")
                .seatClass("Executive")
                .seatNumber("1A")
                .status(Ticket.TicketStatus.CHECKED_IN)
                .build();
        futureTicket = Ticket.builder()
                .id(3L)
                .flightId(3L)
                .flightTime(mockFuture)
                .purchaserId(customerId)
                .passengerName("Test Customer")
                .seatClass("Executive")
                .seatNumber("1A")
                .status(Ticket.TicketStatus.PURCHASED)
                .build();
        cancelledTicket = Ticket.builder()
                .id(4L)
                .flightId(4L)
                .flightTime(mockFuture)
                .purchaserId(customerId)
                .passengerName("Test Customer")
                .seatClass("Executive")
                .seatNumber("1A")
                .status(Ticket.TicketStatus.CANCELLED)
                .build();

    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(repository);
    }

    @Test
    void test_getPastTicketsByCustomerId_ReturnsPastTickets() {
        when(repository.findByPurchaserId(customerId))
                .thenReturn(List.of(firstTicket, pastTicket, futureTicket));

        var tickets = service.getPastTicketsByCustomerId(customerId);
        var expectedTickets = List.of(firstTicket, pastTicket);

        assertEquals(expectedTickets, tickets);
    }

    @Test
    void test_getUpcomingTicketsByCustomerId_ReturnsFutureTickets() {
        when(repository.findByPurchaserId(customerId))
                .thenReturn(List.of(firstTicket, pastTicket, futureTicket));

        var tickets = service.getUpcomingTicketsByCustomerId(customerId);
        var expectedTickets = List.of(futureTicket);

        assertEquals(expectedTickets, tickets);
    }

    @Test
    void test_getTicketById_ReturnsExpectedTicket() {
        when(repository.findById(firstTicket.getId())).thenReturn(Optional.of(firstTicket));

        var ticket = service.getTicketById(firstTicket.getId());
        assertEquals(firstTicket, ticket);
    }

    @Test
    void test_getTicketById_ThrowsNoSuchTicketExceptionOnNonexistentTicketId() {
        assertThrows(NoSuchTicketException.class, () ->
                service.getTicketById(-1L));
    }

    @Test
    void test_checkIn_ChecksTicketIn() {
        when(repository.findById(futureTicket.getId())).thenReturn(Optional.of(futureTicket));

        service.checkIn(futureTicket.getId());
        var ticket = service.getTicketById(futureTicket.getId());
        assertEquals(Ticket.TicketStatus.CHECKED_IN, futureTicket.getStatus());
    }

    @Test
    void test_cancelTicket_CancelsTicket() {
        when(repository.findById(futureTicket.getId())).thenReturn(Optional.of(futureTicket));

        service.cancelTicket(futureTicket.getId());
        var ticket = service.getTicketById(futureTicket.getId());
        assertEquals(Ticket.TicketStatus.CANCELLED, futureTicket.getStatus());
    }

    @Test
    void test_cancelTicket_ThrowsExceptionOnAlreadyCancelledTicket() {
        when(repository.findById(cancelledTicket.getId())).thenReturn(Optional.of(cancelledTicket));

        assertThrows(BadStatusUpdateException.class, () ->
                service.cancelTicket(cancelledTicket.getId()));
    }

    @Test
    void test_getAllTickets_ReturnsAllTickets() {
        when(repository.findAll()).thenReturn(List.of(firstTicket, pastTicket, futureTicket));

        var tickets = service.getAllTickets();
        var expectedTickets = List.of(firstTicket, pastTicket, futureTicket);

        assertEquals(expectedTickets, tickets);
    }

    @Test
    void test_purchaseTickets_returnsPurchasedTickets() {
        PurchaseTicketDto mockDto = PurchaseTicketDto.builder()
                .flightId(1L)
                .purchaserId(firstTicket.getPurchaserId())
                .tickets(List.of(TicketItem.builder()
                        .seatClass(firstTicket.getSeatClass())
                        .seatNumber(firstTicket.getSeatNumber())
                        .passengerName(firstTicket.getPassengerName())
                        .build()))
                .build();

        when(repository.save(mockDto.mapToEntity().get(0))).thenReturn(firstTicket);

        var purchasedTickets = service.purchaseTickets(mockDto);
        var expectedTickets = List.of(firstTicket);

        assertEquals(expectedTickets, purchasedTickets);
    }

}
