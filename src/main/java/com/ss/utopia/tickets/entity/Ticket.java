package com.ss.utopia.tickets.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private Long flightId;

  @NotNull
  private UUID purchaserId;

  @NotBlank
  private String passengerName;

  @NotBlank
  private String seatClass;

  @NotBlank
  private String seatNumber;

  @NotNull
  private TicketStatus status;

  public enum TicketStatus {
    PURCHASED,
    CHECKED_IN,
    CANCELLED,
    REFUNDED;

    TicketStatus fromString(String ticketStatus) {
      return TicketStatus.valueOf(ticketStatus.toUpperCase());
    }
  }
}
