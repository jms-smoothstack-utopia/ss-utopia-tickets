package com.ss.utopia.tickets.security;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.repository.TicketsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerAuthenticationManager {

  private final TicketsRepository ticketsRepository;

  public boolean customerIdMatches(Authentication authentication, PurchaseTicketDto dto) {
    try {
      var jwtPrincipal = (JwtPrincipal) authentication.getPrincipal();
      return jwtPrincipal.getUserId().equals(dto.getPurchaserId());
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
}
