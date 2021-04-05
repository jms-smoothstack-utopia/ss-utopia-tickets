package com.ss.utopia.tickets.client;

import java.util.List;
import com.ss.utopia.tickets.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public interface EmailClient {

  void sendPurchaseTicketConfirmation(String recipientEmail, List<Ticket> ticketsPurchased);
}
