package com.org.server.Mail;


import ch.qos.logback.core.joran.conditional.ThenAction;
import com.org.server.exception.MoiraException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.RequestBody;
import org.thymeleaf.spring6.SpringTemplateEngine;

@RequiredArgsConstructor
public abstract class AbstractMailSend<T> implements MailSend<T>{

    @Value("${spring.mail.username}")
    protected String sender;

    protected final JavaMailSender javaMailSender;
    protected final SpringTemplateEngine templateEngine;

    protected abstract String makeSubject();
    protected abstract  String makeBody(String email,T data);

    @Override
    public void sendMail(String email,T data) {
        try{
            String subject=makeSubject();
            String body=makeBody(email,data);
            MimeMessage message=createMimeMsg(email,subject,body);
            javaMailSender.send(message);

        }
        catch (Exception e){
            throw new MoiraException("서버에러 발생",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MimeMessage createMimeMsg(String email,String subject,String body){
        MimeMessage message=javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(sender);
            helper.setSubject(subject);
            helper.setTo(email);
            helper.setText(body,true);
        }
        catch (Exception e){
            throw new MoiraException("서버에러 발생", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return message;
    };
}
