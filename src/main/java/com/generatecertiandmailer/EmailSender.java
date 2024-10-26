package com.generatecertiandmailer;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

public class EmailSender {

    public boolean sendEmail(String recipientEmail, String subject, String bodyText, String attachmentPath, String fromEmail, String password)throws AuthenticationFailedException {


        //Mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); //SMTP server
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            //Email body
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(bodyText);

            //Check if the attachment file exists
            File attachmentFile = new File(attachmentPath);
            if (!attachmentFile.exists()) {
                throw new IllegalArgumentException("Attachment file not exist: " + attachmentPath);
            }

            //Email attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(attachmentPath));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            //Send Email
            Transport.send(message);
            System.out.println("Email sent successfully to " + recipientEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            throw new AuthenticationFailedException("Authentication failed: Invalid username or password.");
        } catch (SendFailedException e) {
            System.err.println("Failed to send email to " + recipientEmail + ": Invalid recipient address.");
        } catch (MessagingException e) {
            System.err.println("An error occurred while sending the email: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
        return  false;
    }
}
