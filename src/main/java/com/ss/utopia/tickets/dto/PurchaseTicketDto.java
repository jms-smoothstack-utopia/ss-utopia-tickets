package com.ss.utopia.tickets.dto;

import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.entity.Ticket.TicketStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseTicketDto {

  @NotNull
  private UUID purchaserId;

  @NotNull
  private Long flightId;

  @NotNull
  private String email;

  @NotEmpty
  private List<TicketItem> tickets;

  /**
   * @return a List of tickets which we for booking a ticket
   */
  public List<Ticket> mapToEntity() {
    return tickets.stream()
        .map(ticket -> Ticket.builder()
            .purchaserId(purchaserId)
            .flightId(flightId)
            .passengerName(ticket.getPassengerName())
            .seatClass(ticket.getSeatClass())
            .seatNumber(ticket.getSeatNumber())
            .flightTime(ticket.getFlightTime())
            .status(TicketStatus.PURCHASED)
            .build())
        .collect(Collectors.toList());
  }
}
