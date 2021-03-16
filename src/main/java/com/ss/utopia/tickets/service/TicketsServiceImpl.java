package com.ss.utopia.tickets.service;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.entity.Ticket.TicketStatus;
import com.ss.utopia.tickets.exception.NoSuchTicketException;
import com.ss.utopia.tickets.repository.TicketsRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketsServiceImpl implements TicketService {

  private final TicketsRepository repository;

  @Override
  public List<Ticket> getAllTickets() {
    return repository.findAll();
  }

  @Override
  public List<Ticket> getPastTicketsByCustomerId(UUID customerId) {
    return repository.findByPurchaserId(customerId)
            .stream()
            .filter(thisTicket -> thisTicket.getFlightTime().isBefore(ZonedDateTime.now()))
            .collect(Collectors.toList());
  }

  @Override
  public List<Ticket> getUpcomingTicketsByCustomerId(UUID customerId) {
    return repository.findByPurchaserId(customerId)
            .stream()
            .filter(thisTicket -> thisTicket.getFlightTime().isAfter(ZonedDateTime.now()))
            .collect(Collectors.toList());
  }

  @Override
  public Ticket getTicketById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchTicketException(id));
  }

  @Override
  public List<Ticket> purchaseTickets(PurchaseTicketDto purchaseTicketDto) {
    return purchaseTicketDto.mapToEntity()
        .stream()
        .map(repository::save)
        .collect(Collectors.toList());
  }

  @Override
  public void checkIn(Long ticketId) {
    var ticket = getTicketById(ticketId);
    ticket.setStatus(TicketStatus.CHECKED_IN);
    repository.save(ticket);
  }
}
