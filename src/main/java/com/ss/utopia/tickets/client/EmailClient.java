package com.ss.utopia.tickets.client;

import com.ss.utopia.tickets.entity.Ticket;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface EmailClient {

  void sendPurchaseTicketConfirmation(String recipientEmail, List<Ticket> ticketsPurchased);
}
