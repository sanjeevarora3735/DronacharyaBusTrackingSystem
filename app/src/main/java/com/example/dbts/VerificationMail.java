package com.example.dbts;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VerificationMail<handler> {

    private final int Verification_Code;

    VerificationMail() {
        Verification_Code = GenerateOneTimePassword();
    }

    int GenerateOneTimePassword() {
        Random rand = new Random();
        int resRandom = rand.nextInt((9999 - 100) + 1) + 10;
        return resRandom;
    }

    public int getVerification_Code() {
        return Verification_Code;
    }

    void ExecuteVerificationMail(String recipient_Email, int GeneratedOTP) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code

                // email ID of Recipient.
                String recipient = "recipient@gmail.com";
                String host = "smtp.gmail.com";
                Properties properties = System.getProperties();
                // Properties Section
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", "465");
                properties.put("mail.smtp.ssl.enable", "true");
                properties.put("mail.smtp.auth", "true");
                //Session Object
                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("sanjeevarora.developer@gmail.com", "zgjakvzdsoddwjfc");
                    }
                });

                session.setDebug(true);
                // Compose The Message
                MimeMessage mimeMessage = new MimeMessage(session);
                try {
                    mimeMessage.setFrom("sanjeevarora.developer@gmail.com");
                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient_Email));
                    mimeMessage.setSubject("COLLEGE BUS TREKR : EMAIL VERIFICATION");
                    mimeMessage.setText("Please verify your email using by entering the given OTP : " + GeneratedOTP);
                    Transport.send(mimeMessage);
                } catch (MessagingException e) {
                    Log.d("Verification Mail : ", "ExecuteVerificationMail: " + e.getMessage());
                }
            }

        });
    }
}
