package backend.Entity.receipt;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import backend.Entity.TicketInfo;
import java.util.stream.Collectors;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.IOException;






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

}