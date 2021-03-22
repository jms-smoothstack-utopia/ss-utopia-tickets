package com.ss.utopia.tickets.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketItem {

  @NotBlank
  private String seatClass;
  @NotBlank
  private String seatNumber;
  @NotBlank
  private String passengerName;
  @NotBlank
  private ZonedDateTime flightTime;
}
