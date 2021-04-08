package com.ss.utopia.tickets.exception;

import com.stripe.exception.StripeException;
import com.stripe.model.StripeError;

public class CaughtStripeException extends RuntimeException {

  private final StripeError error;
  private final String stripeCode;
  private final String stripeErrorType;

  public CaughtStripeException(StripeException ex) {
    super("Caught a Stripe exception: " + ex.getMessage());
    this.error = ex.getStripeError();
    this.stripeCode = this.error.getCode();
    this.stripeErrorType = this.error.getType();
  }

  public StripeError getError() {
    return error;
  }

  public String getStripeCode() {
    return stripeCode;
  }

  public String getStripeErrorType() {
    return stripeErrorType;
  }
}
