package com.ss.utopia.tickets.service;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.entity.Ticket;
import java.util.List;

public interface TicketService {

  List<Ticket> getAllTickets();

  Ticket getTicketById(Long id);

  List<Ticket> purchaseTickets(PurchaseTicketDto purchaseTicketDto);

  void checkIn(Long ticketId);
}
