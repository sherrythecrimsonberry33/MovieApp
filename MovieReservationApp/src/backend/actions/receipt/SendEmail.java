package backend.actions.receipt;

import backend.actions.TicketInfo;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.stream.Collectors;

public class SendEmail {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "acmeplex@theater.com";
    private static final String SENDER_PASSWORD = "your_app_specific_password";

    public void sendTicketAndReceipt(String recipientEmail, TicketInfo ticketInfo, GenerateReceipt receipt) 
            throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject("Your AcmePlex Movie Tickets and Receipt");

        Multipart multipart = new MimeMultipart();

        BodyPart ticketPart = new MimeBodyPart();
        ticketPart.setContent(formatTicketEmail(ticketInfo), "text/html");
        multipart.addBodyPart(ticketPart);

        BodyPart receiptPart = new MimeBodyPart();
        receiptPart.setContent(formatReceiptEmail(receipt), "text/html");
        multipart.addBodyPart(receiptPart);

        message.setContent(multipart);
        Transport.send(message);
    }

    private String formatTicketEmail(TicketInfo ticketInfo) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h2>Your Movie Tickets</h2>");
        html.append("<p>Ticket ID: ").append(ticketInfo.getTicketId()).append("</p>");
        html.append("<p>Movie: ").append(ticketInfo.getMovieName()).append("</p>");
        html.append("<p>Theater: ").append(ticketInfo.getMovieHallName()).append("</p>");
        html.append("<p>Show Time: ").append(ticketInfo.getShowTiming()).append("</p>");
        html.append("<p>Seats: ").append(String.join(", ", ticketInfo.getSeats().stream()
                .map(seat -> seat.getSeatLabel())
                .collect(Collectors.toList()))).append("</p>");
        html.append("<br><p>Please arrive at least 15 minutes before showtime.</p>");
        html.append("</body></html>");
        return html.toString();
    }

    private String formatReceiptEmail(GenerateReceipt receipt) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h3>Receipt</h3>");
        html.append("<pre>").append(receipt.generateReceiptContent()).append("</pre>");
        html.append("</body></html>");
        return html.toString();
    }
}