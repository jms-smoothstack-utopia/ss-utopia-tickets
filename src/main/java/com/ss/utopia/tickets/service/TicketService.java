package com.ss.utopia.tickets.service;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.entity.Ticket;
import java.util.List;
import java.util.UUID;

public interface TicketService {

  List<Ticket> getAllTickets();

  List<Ticket> getPastTicketsByCustomerId(UUID customerId);

  List<Ticket> getUpcomingTicketsByCustomerId(UUID customerId);

  Ticket getTicketById(Long id);

  List<Ticket> purchaseTickets(PurchaseTicketDto purchaseTicketDto);

  void checkIn(Long ticketId);
}
