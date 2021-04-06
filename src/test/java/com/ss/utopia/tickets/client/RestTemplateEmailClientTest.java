package com.ss.utopia.tickets.client;

import com.ss.utopia.tickets.client.email.TicketConfirmationEmail;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.exception.EmailNotSentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RestTemplateEmailClientTest {

    RestTemplateBuilder restTemplateBuilder = Mockito.mock(RestTemplateBuilder.class);
    RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    RestTemplateEmailClient emailClient;

    String mockBookedFlights = "http://localhost:4200/flights/upcoming";
    String mockSesEndpoint = "http://asdf.aws.com/email";

    @BeforeEach
    void beforeEach(){
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        emailClient = new RestTemplateEmailClient();
        emailClient.setBuilder(restTemplateBuilder);
        emailClient.postConstruct();

        Mockito.verify(restTemplateBuilder).build();

        emailClient.setSesEndpoint(mockSesEndpoint);
        emailClient.setTicketsBaseUrl(mockBookedFlights);
    }

    @Test
    void test_sendPurchaseTicketEmail_successfullySendEmailToThatUser(){
        String mockEmail = "test@test.com";
        var ticketsPurchased = List.of(Ticket.builder().build());

        when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(TicketConfirmationEmail.class), Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.ok("example response"));

        assertDoesNotThrow(() -> emailClient.sendPurchaseTicketConfirmation(mockEmail, ticketsPurchased));
    }

    @Test
    void test_sendPurchaseTicketEmail_emailNotSentThrowsError(){
        String mockEmail = "test@test.com";
        var ticketsPurchased = List.of(Ticket.builder().build());

        when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(TicketConfirmationEmail.class), Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.notFound().build());

        assertThrows(EmailNotSentException.class, () -> emailClient.sendPurchaseTicketConfirmation(mockEmail, ticketsPurchased));
    }

    @Test
    void test_sendPurchaseTicketEmail_responseIsNull(){
        String mockEmail = "test@test.com";
        var ticketsPurchased = List.of(Ticket.builder().build());

        when(restTemplate.postForEntity(Mockito.anyString(), Mockito.any(TicketConfirmationEmail.class), Mockito.eq(String.class)))
                .thenReturn(null);

        assertThrows(EmailNotSentException.class, () -> emailClient.sendPurchaseTicketConfirmation(mockEmail, ticketsPurchased));
    }
}
