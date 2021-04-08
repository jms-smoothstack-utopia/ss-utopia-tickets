package com.ss.utopia.tickets.dto;

import com.stripe.param.PaymentIntentCreateParams;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateDto {

  @NotNull
  private String email;

  @NotNull
  private Long amount;
  //all payments in USD and by card

  public PaymentIntentCreateParams buildPaymentCreate() {
    return PaymentIntentCreateParams.builder()
            .setAmount(amount)
            .setReceiptEmail(email)
            .setCurrency("usd")
            .addPaymentMethodType("card")
            .build();
  }
}
