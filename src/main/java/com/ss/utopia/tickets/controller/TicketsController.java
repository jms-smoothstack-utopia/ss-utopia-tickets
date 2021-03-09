package com.ss.utopia.tickets.controller;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.security.permissions.AdminOnlyPermission;
import com.ss.utopia.tickets.security.permissions.EmployeeOnlyPermission;
import com.ss.utopia.tickets.service.TicketService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(EndpointConstants.API_V_0_1_TICKETS)
@RequiredArgsConstructor
public class TicketsController {

  private final TicketService service;

  @AdminOnlyPermission
  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Ticket>> getAllTickets() {
    log.info("GET all");
    List<Ticket> tickets = service.getAllTickets();
    if (tickets.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(tickets);
  }

  @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','TRAVEL_AGENT')"
      + " OR @customerAuthenticationManager.customerIdMatches(#id)")
  @GetMapping(value = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    log.info("GET id=" + id);
    return ResponseEntity.of(Optional.ofNullable(service.getTicketById(id)));
  }

  //todo limit to only customer?
  @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','TRAVEL_AGENT')"
      + " OR @customerAuthenticationManager.customerIdMatches(#purchaseTicketDto)")
  @PostMapping
  public ResponseEntity<List<Ticket>> purchaseTickets(@Valid @RequestBody PurchaseTicketDto purchaseTicketDto) {
    log.info("POST new ticket");
    var tickets = service.purchaseTickets(purchaseTicketDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(tickets);
  }

  @EmployeeOnlyPermission
  @PutMapping("/{id}")
  public ResponseEntity<?> checkIn(@PathVariable Long id) {
    log.info("PUT id=" + id);
    //todo add ability to update multiple tickets
    // also, should take a DTO of the status and not be limited to just checkin
    service.checkIn(id);
    return ResponseEntity.noContent().build();
  }
}
