package com.ss.utopia.tickets.exception;

import com.ss.utopia.tickets.entity.Ticket.TicketStatus;

public class BadStatusUpdateException extends IllegalStateException {

  private final Long ticketId;
  private final TicketStatus currentStatus;
  private final TicketStatus requestedStatus;

  public BadStatusUpdateException(Long ticketId,
                                  TicketStatus currentStatus,
                                  TicketStatus requestedStatus) {
    super("Cannot update ticket id=" + ticketId + " to status " + requestedStatus
            + ", already " + currentStatus);
    this.ticketId = ticketId;
    this.currentStatus = currentStatus;
    this.requestedStatus = requestedStatus;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public TicketStatus getCurrentStatus() {
    return currentStatus;
  }

  public TicketStatus getRequestedStatus() {
    return requestedStatus;
  }
}
