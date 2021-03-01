package com.ss.utopia.tickets.service;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.entity.Ticket.TicketStatus;
import com.ss.utopia.tickets.exception.NoSuchTicketException;
import com.ss.utopia.tickets.repository.TicketsRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TicketsServiceImpl implements TicketService {

  private final TicketsRepository repository;

  public TicketsServiceImpl(TicketsRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<Ticket> getAllTickets() {
    return repository.findAll();
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
