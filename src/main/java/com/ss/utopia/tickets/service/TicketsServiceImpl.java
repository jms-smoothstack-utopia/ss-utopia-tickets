package com.ss.utopia.tickets.service;

import com.ss.utopia.tickets.dto.PaymentCreateDto;
import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.entity.Ticket.TicketStatus;
import com.ss.utopia.tickets.exception.BadStatusUpdateException;
import com.ss.utopia.tickets.exception.CaughtStripeException;
import com.ss.utopia.tickets.exception.NoSuchTicketException;
import com.ss.utopia.tickets.repository.TicketsRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "com.ss.utopia.tickets.service")
public class TicketsServiceImpl implements TicketService {

  private final TicketsRepository repository;

  //a setter is needed for Spring Boot to bind a configuration property
  @Getter @Setter
  private String stripeKey;

  @Override
  public List<Ticket> getAllTickets() {
    return repository.findAll();
  }

  @Override
  public List<Ticket> getPastTicketsByCustomerId(UUID customerId) {
    return repository.findByPurchaserId(customerId)
            .stream()
            .filter(thisTicket -> thisTicket.getFlightTime().isBefore(ZonedDateTime.now()))
            .collect(Collectors.toList());
  }

  @Override
  public List<Ticket> getUpcomingTicketsByCustomerId(UUID customerId) {
    return repository.findByPurchaserId(customerId)
            .stream()
            .filter(thisTicket -> thisTicket.getFlightTime().isAfter(ZonedDateTime.now()))
            .collect(Collectors.toList());
  }

  @Override
  public Ticket getTicketById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new NoSuchTicketException(id));
  }

  @Override
  public List<Ticket> purchaseTickets(PurchaseTicketDto purchaseTicketDto) {
    //call initiatePayment and such here
    return purchaseTicketDto.mapToEntity()
        .stream()
        .map(repository::save)
        .collect(Collectors.toList());
  }

  @Override
  public void cancelTicket(Long ticketId) {
    var ticket = getTicketById(ticketId);
    var status = ticket.getStatus();
    if (status == TicketStatus.CANCELLED) {
      throw new BadStatusUpdateException(ticketId, status, TicketStatus.CANCELLED);
    }
    ticket.setStatus(TicketStatus.CANCELLED);
    //for future payment integration: fire off a refund request to the processor here
    repository.save(ticket);
  }

  @Override
  public PaymentIntent initiatePayment(PaymentCreateDto paymentCreateDto) {
    PaymentIntentCreateParams createParams = paymentCreateDto.buildPaymentCreate();
    RequestOptions options = RequestOptions.builder().setApiKey(this.stripeKey).build();
    try {
      return PaymentIntent.create(createParams, options);
    } catch (StripeException e) {
      throw new CaughtStripeException(e);
    }
  }

  @Override
  public void checkIn(Long ticketId) {
    var ticket = getTicketById(ticketId);
    ticket.setStatus(TicketStatus.CHECKED_IN);
    repository.save(ticket);
  }

}
