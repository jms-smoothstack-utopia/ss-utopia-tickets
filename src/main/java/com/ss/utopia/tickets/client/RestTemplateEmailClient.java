package com.ss.utopia.tickets.client;

import com.ss.utopia.tickets.client.email.AbstractUrlEmail;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.exception.EmailNotSentException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ss.utopia.tickets.client.email.TicketConfirmationEmail;
import javax.annotation.PostConstruct;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@ConfigurationProperties(value = "com.ss.utopia.email", ignoreUnknownFields = false)
public class RestTemplateEmailClient implements EmailClient {

  @Setter
  private String sesEndpoint;

  @Setter
  private String ticketsBaseURL;

  private RestTemplateBuilder builder;
  private RestTemplate restTemplate;

  @Autowired
  public void setBuilder(RestTemplateBuilder builder) {
    this.builder = builder;
  }

  @PostConstruct
  public void postConstruct() {
    restTemplate = builder.build();
  }

  @Override
  public void sendPurchaseTicketConfirmation(String recipientEmail, List<Ticket> ticketsPurchased) {
    var email = new TicketConfirmationEmail(recipientEmail, ticketsBaseURL);
    var response = restTemplate.postForEntity(sesEndpoint, email, String.class);
    handleResponse(response, email);
  }

  private void handleResponse(ResponseEntity<String> response, AbstractUrlEmail email) {
    if (response == null) {
      throw new EmailNotSentException("NULL RESPONSE", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (response.getStatusCode().is2xxSuccessful()) {
      log.debug("Email sent to: " + email.getRecipient());
      log.debug(email.getSubject());
    } else {
      log.error("Unable to send confirmation email.");
      log.error("Status code: " + response.getStatusCode().value());
      log.error("Response body: " + response.getBody());
      throw new EmailNotSentException(response.getBody(), response.getStatusCode());
    }
  }
}
