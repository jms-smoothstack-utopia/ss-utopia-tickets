package com.ss.utopia.tickets.exception;

import java.util.NoSuchElementException;

public class NoSuchTicketException extends NoSuchElementException {

  private final Long ticketId;

  public NoSuchTicketException(Long ticketId) {
    super("No ticket record for id=" + ticketId);
    this.ticketId = ticketId;
  }

  public Long getTicketId() {
    return ticketId;
  }
}
