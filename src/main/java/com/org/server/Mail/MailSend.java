package com.org.server.Mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestControllerAdvice;


public interface MailSend<T> {


    EmailType checkType();
    void sendMail(String email,T data);
}
