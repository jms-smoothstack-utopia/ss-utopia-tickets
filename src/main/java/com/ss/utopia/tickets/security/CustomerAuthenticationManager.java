package com.ss.utopia.tickets.security;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.repository.TicketsRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerAuthenticationManager {

  private final TicketsRepository ticketsRepository;

  public boolean customerIdMatches(Authentication authentication, PurchaseTicketDto dto) {
    var jwtOwnerId = UUID.fromString((String) authentication.getDetails());
    return jwtOwnerId.equals(dto.getPurchaserId());
  }

  public boolean customerIdMatches(Authentication authentication, Long ticketId) {
    var jwtOwnerId = UUID.fromString((String) authentication.getDetails());
    return ticketsRepository.findById(ticketId)
        .map(t -> t.getPurchaserId().equals(jwtOwnerId))
        .orElse(false);
  }
}
