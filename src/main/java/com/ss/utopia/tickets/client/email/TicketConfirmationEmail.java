package com.ss.utopia.tickets.client.email;

public class TicketConfirmationEmail extends AbstractUrlEmail {

    private static final String DEFAULT_SUBJECT = "Utopia Tickets Confirmation";
    private static final String DEFAULT_MESSAGE =
            "Thanks for purchasing tickets from us! Please use this link to look at your new upcoming flights!";

    public TicketConfirmationEmail(String recipient, String url) {
        this(recipient, DEFAULT_SUBJECT, DEFAULT_MESSAGE, url);
    }

    public TicketConfirmationEmail(String recipient, String subject, String message, String url) {
        super(recipient, subject, message, url);
    }
}
