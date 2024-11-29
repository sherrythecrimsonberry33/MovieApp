package backend.Entity.receipt;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import backend.Entity.TicketInfo;

import java.util.stream.Collectors;

public class SendEmail {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "alexxcarter92@gmail.com";
    private static final String SENDER_PASSWORD = "kcaoagymqzbuplkb";

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
        html.append("<p>Movie Hall: ").append(ticketInfo.getMovieHallName()).append("</p>");
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

    
    public void sendCancellationEmail(String recipientEmail) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS
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
        message.setSubject("Your AcmePlex Ticket Cancellation");

        String emailContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                              "<h2>Ticket Cancellation Confirmation</h2>" +
                              "<p>Dear Customer,</p>" +
                              "<p>Your ticket has been successfully canceled.</p>" +
                              "<p>If you have any questions or need further assistance, please contact our support team.</p>" +
                              "<br><p>Thank you for choosing AcmePlex Theaters!</p>" +
                              "</body></html>";

        message.setContent(emailContent, "text/html");

        Transport.send(message);
    }
}