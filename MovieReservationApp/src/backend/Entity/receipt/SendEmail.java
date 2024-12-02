package backend.Entity.receipt;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import backend.Entity.Movie;
import backend.Entity.TicketInfo;
import backend.database.DatabaseConnection;

import java.util.stream.Collectors;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.sql.Statement;




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
        try {
            message.setFrom(new InternetAddress(SENDER_EMAIL, "AcmePlex Cinema"));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Failed to set sender email address", e);
        }
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

    
 


    public void sendCancellationEmailWithRefund(String recipientEmail, String ticketId, 
                                              double totalAmount, double refundAmount, 
                                              boolean isRegisteredUser) throws MessagingException {
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
        message.setSubject("Your AcmePlex Ticket Cancellation - Refund Details");

        String refundSection = isRegisteredUser ? 
            String.format(
                "<div style='margin: 20px 0; padding: 15px; background-color: #f8f8f8; border-radius: 5px;'>" +
                "<h3 style='color: #28a745; margin-bottom: 10px;'>Refund Details</h3>" +
                "<p>Original Amount: $%.2f</p>" +
                "<p><strong>Full Refund Amount: $%.2f</strong></p>" +
                "<p style='color: #28a745;'><em>As a registered user, no cancellation fee applies.</em></p>" +
                "</div>",
                totalAmount, refundAmount
            ) :
            String.format(
                "<div style='margin: 20px 0; padding: 15px; background-color: #f8f8f8; border-radius: 5px;'>" +
                "<h3 style='color: #28a745; margin-bottom: 10px;'>Refund Details</h3>" +
                "<p>Original Amount: $%.2f</p>" +
                "<p>Cancellation Fee (15%%): $%.2f</p>" +
                "<p><strong>Refund Amount: $%.2f</strong></p>" +
                "</div>",
                totalAmount,
                totalAmount * 0.15,
                refundAmount
            );

        String emailContent = String.format(
            "<html>" +
            "<body style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>" +
            "<h2 style='color: #e50914;'>Ticket Cancellation Confirmation</h2>" +
            "<p>Dear Customer,</p>" +
            "<p>Your ticket <strong>%s</strong> has been successfully canceled.</p>" +
            "%s" + // refund section
            "<p>The refund will be processed to your original payment method within 3-5 business days.</p>" +
            "<hr style='margin: 20px 0; border: none; border-top: 1px solid #eee;'>" +
            "<p>If you have any questions about your refund, please contact our support team.</p>" +
            "<p style='color: #666;'><em>Thank you for choosing AcmePlex Theaters!</em></p>" +
            "</body>" +
            "</html>",
            ticketId,
            refundSection
        );

        message.setContent(emailContent, "text/html");
        Transport.send(message);
    }

    public void sendAnnualFeeInvoice(String recipientEmail, String firstName, String lastName, String address) throws MessagingException {
        try {
            // Generate PDF invoice
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            
            document.open();
            
            // Add logo using relative path from assets
        try {
            String logoPath = "MovieReservationApp" + File.separator + "assets" + File.separator + "AcmePlex.png";
            Image logo = Image.getInstance(logoPath);
            logo.scaleToFit(100, 100);
            logo.setAlignment(Element.ALIGN_RIGHT);
            document.add(logo);
        } catch (IOException | DocumentException e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
            // Add company name
            document.add(new Paragraph("AcmePlex Theaters"));
            document.add(new Paragraph("123 Main Street"));
            

            document.add(Chunk.NEWLINE);

            // Add invoice header
      
            document.add(new Paragraph("INVOICE"));
            document.add(Chunk.NEWLINE);

            // Add invoice details
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            String currentDate = LocalDateTime.now().format(formatter);

            document.add(new Paragraph("Date: " + currentDate));
            document.add(new Paragraph("Invoice #: AF-" + System.currentTimeMillis()));
            document.add(Chunk.NEWLINE);

            // Add customer details
            document.add(new Paragraph("Bill To:"));
            document.add(new Paragraph(firstName + " " + lastName));
            document.add(new Paragraph(address));
            document.add(Chunk.NEWLINE);
            
            // Create table for invoice items
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            
            // Add table headers
            table.addCell("Description");
            table.addCell("Quantity");
            table.addCell("Unit Price");
            table.addCell("Total");
            
            // Add invoice item
            table.addCell("Annual Membership Fee");
            table.addCell("1");
            table.addCell("$20.00");
            table.addCell("$20.00");
            
            document.add(table);
            document.add(Chunk.NEWLINE);
            
            // Add total
            Paragraph total = new Paragraph("Total Amount: $20.00");
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            
            // Add footer
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Thank you for becoming a registered member!"));
            document.close();
            
            // Send email with PDF attachment
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
            message.setFrom(new InternetAddress(SENDER_EMAIL, "AcmePlex Cinema"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("AcmePlex Annual Membership Fee Invoice");
            
            // Create multipart message
            Multipart multipart = new MimeMultipart();
            
            // Add text part
            BodyPart textPart = new MimeBodyPart();
            textPart.setContent(
                "Dear " + firstName + ",<br><br>" +
                "Thank you for becoming a registered member of AcmePlex Theaters! " +
                "Please find attached your invoice for the annual membership fee.<br><br>" +
                "Best regards,<br>" +
                "AcmePlex Theaters", 
                "text/html"
            );
            multipart.addBodyPart(textPart);
            
            // Add PDF attachment
            BodyPart pdfPart = new MimeBodyPart();
            pdfPart.setContent(outputStream.toByteArray(), "application/pdf");
            pdfPart.setFileName("AcmePlex_Annual_Fee_Invoice.pdf");
            multipart.addBodyPart(pdfPart);
            
            message.setContent(multipart);
            Transport.send(message);
            
        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace
            System.err.println("Detailed error while sending invoice: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Caused by: " + e.getCause().getMessage());
            }
            throw new MessagingException("Failed to generate and send invoice: " + e.getMessage(), e);
        }
    }


    public void sendMovieAnnouncement(String subject, String messageContent) throws MessagingException {
        // Step 1: Retrieve all registered user emails from the database
        List<String> recipientEmails = getAllRegisteredUserEmails();

        // Step 2: Set up email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Step 3: Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        // Step 4: Create the email message
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(SENDER_EMAIL, "AcmePlex Cinema"));
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Failed to set sender email address", e);
        }
        message.setSubject(subject);
        message.setContent(messageContent, "text/html");

        // Step 5: Send the email to all recipients
        for (String recipientEmail : recipientEmails) {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            Transport.send(message);
        }
    }


    // Helper method to retrieve all registered user emails
    private List<String> getAllRegisteredUserEmails() {
        List<String> emails = new ArrayList<>();
        String query = "SELECT email FROM registered_users";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                emails.add(rs.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

    //Sends an email announcement to all registered users about the new movie.
    public void sendMovieAnnouncementToUsers(Movie movie) {
        SendEmail emailSender = new SendEmail();

        String subject = "New Movie Release: " + movie.getTitle();

        String messageContent = String.format(
            "<html>" +
            "<body style='font-family: Arial, sans-serif;'>" +
            "<h2 style='color: #e50914;'>Exciting News from AcmePlex Theaters!</h2>" +
            "<p>We are thrilled to announce a new movie coming to our theaters:</p>" +
            "<h3>%s</h3>" +
            "<img src='%s' alt='%s Poster' style='width:200px; height:auto;'/>" +
            "<p><strong>Genre:</strong> %s</p>" +
            "<p><strong>Synopsis:</strong> %s</p>" +
            "<p><strong>Rating:</strong> %.1f/10</p>" +
            "<p><strong>Duration:</strong> %d minutes</p>" +
            "<p>Book your tickets now on our mobile app!</p>" +
            "<p style='color: #666;'>Thank you for choosing AcmePlex Theaters.</p>" +
            "</body>" +
            "</html>",
            movie.getTitle(),
            movie.getPosterUrl(),
            movie.getTitle(),
            movie.getGenre(),
            movie.getSynopsis(),
            movie.getRating(),
            movie.getDuration()
        );

        try {
            emailSender.sendMovieAnnouncement(subject, messageContent);
            System.out.println("Movie announcement sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to send movie announcement: " + e.getMessage());
        }
    }

}