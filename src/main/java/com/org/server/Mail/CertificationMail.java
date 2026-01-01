package com.org.server.Mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Component
@Slf4j
public class CertificationMail extends AbstractMailSend<String>{

    public CertificationMail(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        super(javaMailSender, templateEngine);

    }
    @Override
    protected String makeSubject() {
        return "회원가입 인증코드 메일입니다";
    }
    @Override
    protected String makeBody(String email,String data) {
        log.info("certification mail 호출");
        Context context=new Context();
        context.setVariable("인증코드",data);
        /*
        * 인증코드 만든느 로직 추가.
        * */
        return templateEngine.process("mail/certification-code",context);
    }
    @Override
    public EmailType checkType() {
        return EmailType.CERTIFICATION;
    }

}
