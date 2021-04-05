package com.ss.utopia.tickets.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.utopia.tickets.dto.PurchaseTicketDto;
import com.ss.utopia.tickets.dto.TicketItem;
import com.ss.utopia.tickets.entity.Ticket;
import com.ss.utopia.tickets.entity.Ticket.TicketStatus;
import com.ss.utopia.tickets.repository.TicketsRepository;
import com.ss.utopia.tickets.security.SecurityConstants;
import com.ss.utopia.tickets.service.TicketService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class TicketsControllerSecurityTests {

    final Date expiresAt = Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC));
    @Autowired
    WebApplicationContext wac;
    @MockBean
    SecurityConstants securityConstants;

    @MockBean
    TicketService ticketService;
    @MockBean
    TicketsRepository ticketsRepository;

    MockMvc mvc;

    LocalDateTime mockFlightTime = LocalDateTime.of(3021, 11, 11, 11, 11);
    ZonedDateTime mockZonedTime = ZonedDateTime.of(mockFlightTime, ZoneId.of("America/New_York"));

    LocalDateTime mockPastTime = LocalDateTime.of(2020, 11, 11, 11, 11);
    ZonedDateTime mockZonedPastTime = ZonedDateTime.of(mockPastTime, ZoneId.of("America/New_York"));

    Ticket mockTicket = Ticket.builder()
            .id(1L)
            .flightId(1L)
            .flightTime(mockZonedTime)
            .purchaserId(UUID.fromString("a4a9feca-bfe7-4c45-8319-7cb6cdd359db"))
            .passengerName("John Tester")
            .seatClass("executive")
            .seatNumber("1A")
            .status(TicketStatus.PURCHASED)
            .build();

    Ticket mockPastTicket = Ticket.builder()
            .id(2L)
            .flightId(1L)
            .flightTime(mockZonedPastTime)
            .purchaserId(UUID.fromString("a4a9feca-bfe7-4c45-8319-7cb6cdd359db"))
            .passengerName("John Tester")
            .seatClass("executive")
            .seatNumber("1A")
            .status(TicketStatus.PURCHASED)
            .build();

    PurchaseTicketDto mockDto = PurchaseTicketDto.builder()
            .flightId(1L)
            .purchaserId(mockTicket.getPurchaserId()).email("test@test.com")
            .tickets(List.of(TicketItem.builder()
                    .seatClass(mockTicket.getSeatClass())
                    .seatNumber(mockTicket.getSeatNumber())
                    .passengerName(mockTicket.getPassengerName())
                    .build()))
            .build();

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();

        Mockito.reset(ticketService);
        Mockito.reset(ticketsRepository);

        when(securityConstants.getEndpoint()).thenReturn("/authenticate");
        when(securityConstants.getJwtIssuer()).thenReturn("test-issuer");
        when(securityConstants.getExpiresAt()).thenReturn(expiresAt);
        when(securityConstants.getJwtSecret()).thenReturn("superSecret");
        when(securityConstants.getUserIdClaimKey()).thenReturn("userId");
        when(securityConstants.getAuthorityClaimKey()).thenReturn("Authorities");
        when(securityConstants.getJwtHeaderName()).thenReturn("Authorization");
        when(securityConstants.getJwtHeaderPrefix()).thenReturn("Bearer ");

        when(ticketService.getAllTickets()).thenReturn(List.of(mockTicket));
        when(ticketService.getTicketById(mockTicket.getId())).thenReturn(mockTicket);
        when(ticketService.purchaseTickets(mockDto)).thenReturn(List.of(mockTicket));
        when(ticketService.getUpcomingTicketsByCustomerId(UUID.fromString(MockUser.MATCH_CUSTOMER.id)))
                .thenReturn(List.of(mockTicket));
        when(ticketService.getPastTicketsByCustomerId(UUID.fromString(MockUser.MATCH_CUSTOMER.id)))
                .thenReturn(List.of(mockPastTicket));

        when(ticketsRepository.findById(mockTicket.getId())).thenReturn(Optional.of(mockTicket));
    }

    String getJwt(MockUser mockUser) {
        var jwt = JWT.create()
                .withSubject(mockUser.email)
                .withIssuer(securityConstants.getJwtIssuer())
                .withClaim(securityConstants.getUserIdClaimKey(), mockUser.id)
                .withClaim(securityConstants.getAuthorityClaimKey(), List.of(mockUser.getAuthority()))
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC512(securityConstants.getJwtSecret()));
        return "Bearer " + jwt;
    }

    @Test
    void test_getAllTickets_CanOnlyBePerformedByAdmin() throws Exception {
        mvc
                .perform(
                        get(EndpointConstants.API_V_0_1_TICKETS)
                                .header("Authorization", getJwt(MockUser.ADMIN)))
                .andExpect(status().isOk());

        var unauthed = List.of(MockUser.DEFAULT,
                MockUser.MATCH_CUSTOMER,
                MockUser.UNMATCH_CUSTOMER,
                MockUser.EMPLOYEE,
                MockUser.TRAVEL_AGENT);

        for (var user : unauthed) {
            mvc
                    .perform(
                            get(EndpointConstants.API_V_0_1_TICKETS)
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isForbidden());
        }

        mvc
                .perform(
                        get(EndpointConstants.API_V_0_1_TICKETS))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_getTicketById_CanOnlyBePerformedByAuthedUsersOrPurchaser() throws Exception {
        var alwaysAuthed = List.of(MockUser.ADMIN,
                MockUser.TRAVEL_AGENT,
                MockUser.EMPLOYEE,
                MockUser.MATCH_CUSTOMER);

        for (var user : alwaysAuthed) {
            mvc
                    .perform(
                            get(EndpointConstants.API_V_0_1_TICKETS + "/" + mockTicket.getId())
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isOk());
        }

        var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);
        for (var user : unauthed) {
            mvc
                    .perform(
                            get(EndpointConstants.API_V_0_1_TICKETS + "/" + mockTicket.getId())
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isForbidden());
        }
        mvc
                .perform(
                        get(EndpointConstants.API_V_0_1_TICKETS + "/" + mockTicket.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_purchaseTickets_CanOnlyBePerformedByAuthedUserOrPurchaser() throws Exception {
        var alwaysAuthed = List.of(MockUser.ADMIN,
                MockUser.TRAVEL_AGENT,
                MockUser.EMPLOYEE,
                MockUser.MATCH_CUSTOMER);

        for (var user : alwaysAuthed) {
            mvc
                    .perform(
                            post(EndpointConstants.API_V_0_1_TICKETS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(new ObjectMapper().writeValueAsString(mockDto))
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isCreated());
        }

        var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);
        for (var user : unauthed) {
            mvc
                    .perform(
                            post(EndpointConstants.API_V_0_1_TICKETS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(new ObjectMapper().writeValueAsString(mockDto))
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isForbidden());
        }

        mvc
                .perform(
                        post(EndpointConstants.API_V_0_1_TICKETS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(mockDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_checkIn_CanOnlyBePerformedByEmployee() throws Exception {
        var alwaysAuthed = List.of(MockUser.ADMIN, MockUser.EMPLOYEE);
        for (var user : alwaysAuthed) {
            mvc
                    .perform(
                            put(EndpointConstants.API_V_0_1_TICKETS + "/" + mockTicket.getId())
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isNoContent());
        }

        var unauthed = List.of(MockUser.TRAVEL_AGENT,
                MockUser.MATCH_CUSTOMER,
                MockUser.UNMATCH_CUSTOMER,
                MockUser.DEFAULT);
        for (var user : unauthed) {
            mvc
                    .perform(
                            put(EndpointConstants.API_V_0_1_TICKETS + "/" + mockTicket.getId())
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isForbidden());
        }

        mvc
                .perform(
                        put(EndpointConstants.API_V_0_1_TICKETS + "/" + mockTicket.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_getUpcomingTicketsForCustomer_CanOnlyBePerformedByAuthedUsersOrPurchaser() throws Exception {
        var alwaysAuthed = List.of(MockUser.ADMIN,
                MockUser.TRAVEL_AGENT,
                MockUser.EMPLOYEE,
                MockUser.MATCH_CUSTOMER);

        for (var user : alwaysAuthed) {
            mvc
                    .perform(
                            get(EndpointConstants.API_V_0_1_TICKETS + "/upcoming/"
                                    + MockUser.MATCH_CUSTOMER.id)
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isOk());
        }

        var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);
        for (var user : unauthed) {
            mvc
                    .perform(
                            get(EndpointConstants.API_V_0_1_TICKETS + "/upcoming/"
                                    + MockUser.MATCH_CUSTOMER.id)
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isForbidden());
        }
        mvc
                .perform(
                        get(EndpointConstants.API_V_0_1_TICKETS + "/upcoming/"
                                + MockUser.MATCH_CUSTOMER.id))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_getPastTicketsForCustomer_CanOnlyBePerformedByAuthedUsersOrPurchaser() throws Exception {
        var alwaysAuthed = List.of(MockUser.ADMIN,
                MockUser.TRAVEL_AGENT,
                MockUser.EMPLOYEE,
                MockUser.MATCH_CUSTOMER);

        for (var user : alwaysAuthed) {
            mvc
                    .perform(
                            get(EndpointConstants.API_V_0_1_TICKETS + "/history/"
                                    + MockUser.MATCH_CUSTOMER.id)
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isOk());
        }

        var unauthed = List.of(MockUser.DEFAULT, MockUser.UNMATCH_CUSTOMER);
        for (var user : unauthed) {
            mvc
                    .perform(
                            get(EndpointConstants.API_V_0_1_TICKETS + "/history/"
                                    + MockUser.MATCH_CUSTOMER.id)
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isForbidden());
        }
        mvc
                .perform(
                        get(EndpointConstants.API_V_0_1_TICKETS + "/history/"
                                + MockUser.MATCH_CUSTOMER.id))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_cancelTicket_CanOnlyBePerformedByAuthedUsersOrPurchaser() throws Exception {
        var alwaysAuthed = List.of(MockUser.ADMIN,
                MockUser.TRAVEL_AGENT,
                MockUser.EMPLOYEE,
                MockUser.MATCH_CUSTOMER);
        for (var user : alwaysAuthed) {
            mvc
                    .perform(
                            put(EndpointConstants.API_V_0_1_TICKETS + "/cancel/"
                                    + mockTicket.getId())
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isNoContent());
        }
        var unauthed = List.of(MockUser.DEFAULT,
                MockUser.UNMATCH_CUSTOMER);
        for (var user : unauthed) {
            mvc
                    .perform(
                            put(EndpointConstants.API_V_0_1_TICKETS + "/cancel/"
                                    + mockTicket.getId())
                                    .header("Authorization", getJwt(user)))
                    .andExpect(status().isForbidden());
        }
        mvc
                .perform(
                        get(EndpointConstants.API_V_0_1_TICKETS + "/cancel/"
                                + mockTicket.getId()))
                .andExpect(status().isForbidden());
    }

    enum MockUser {
        DEFAULT("default@test.com", "ROLE_DEFAULT", UUID.randomUUID().toString()),
        MATCH_CUSTOMER("eddy_grant@test.com", "ROLE_CUSTOMER", "a4a9feca-bfe7-4c45-8319-7cb6cdd359db"),
        UNMATCH_CUSTOMER("someOtherCustomer@test.com", "ROLE_CUSTOMER", UUID.randomUUID().toString()),
        EMPLOYEE("employee@test.com", "ROLE_EMPLOYEE", UUID.randomUUID().toString()),
        TRAVEL_AGENT("travel_agent@test.com", "ROLE_TRAVEL_AGENT", UUID.randomUUID().toString()),
        ADMIN("admin@test.com", "ROLE_ADMIN", UUID.randomUUID().toString());


        final String email;
        final GrantedAuthority grantedAuthority;
        final String id;

        MockUser(String email, String grantedAuthority, String id) {
            this.email = email;
            this.grantedAuthority = new SimpleGrantedAuthority(grantedAuthority);
            this.id = id;
        }

        public String getAuthority() {
            return grantedAuthority.getAuthority();
        }
    }
}
