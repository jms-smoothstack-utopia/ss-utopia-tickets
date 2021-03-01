package com.ss.utopia.tickets.controller;

import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.service.TicketService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketsController {

  private final TicketService service;

  public TicketsController(TicketService service) {
    this.service = service;
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<List<Ticket>> getAllTickets() {
    List<Ticket> tickets = service.getAllTickets();
    if (tickets.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(tickets);
  }

  @GetMapping(value = "/{id}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
  public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
    return ResponseEntity.of(Optional.ofNullable(service.getTicketById(id)));
  }

  @PostMapping
  public ResponseEntity<List<Ticket>> purchaseTickets(@Valid @RequestBody PurchaseTicketDto purchaseTicketDto) {
    var tickets = service.purchaseTickets(purchaseTicketDto);
    return ResponseEntity.status(201).body(tickets);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> checkIn(@PathVariable Long id) {
    //todo add ability to update multiple tickets
    // also, should take a DTO of the status and not be limited to just checkin
    service.checkIn(id);
    return ResponseEntity.noContent().build();
  }
}
