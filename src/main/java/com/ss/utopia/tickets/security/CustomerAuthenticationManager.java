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

  public boolean customerIdMatches(Authentication authentication,
      PurchaseTicketDto purchaseTicketDto) {
    try {
      var jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
      return jwtPrincipal.getUserId().equals(purchaseTicketDto.getPurchaserId());
    } catch (ClassCastException ex) {
      return false;
    }
  }

  public boolean customerIdMatches(Authentication authentication, Long ticketId) {
    try {
      var jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
      return ticketsRepository.findById(ticketId)
          .map(t -> t.getPurchaserId().equals(jwtPrincipal.getUserId()))
          .orElse(false);
    } catch (ClassCastException ex) {
      return false;
    }
  }

  public boolean customerIdMatches(Authentication authentication, UUID customerId) {
    try {
      var jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
      return jwtPrincipal.getUserId().equals(customerId);
    } catch (ClassCastException ex) {
      return false;
    }
  }
}
