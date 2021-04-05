package com.ss.utopia.tickets.client;


import com.ss.utopia.tickets.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmailClient {

  void sendPurchaseTicketConfirmation(String recipientEmail, List<Ticket> ticketsPurchased);
}
