package com.org.server.Mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
public class PassWordMail extends AbstractMailSend<String>{
    public PassWordMail(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        super(javaMailSender, templateEngine);
    }

    @Override
    protected String makeSubject() {
        return "임시 비밀번호 발급 이메일";
    }

    @Override
    protected String makeBody(String email, String data) {
        Context context=new Context();
        context.setVariable("비밀번호",data);
        /*
         * 인증코드 만든느 로직 추가.
         * */
        return templateEngine.process("mail/password",context);
    }

    @Override
    public EmailType checkType() {
        return EmailType.PASSWORD;
    }
}

