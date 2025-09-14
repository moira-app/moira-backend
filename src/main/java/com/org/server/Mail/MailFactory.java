package com.org.server.Mail;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MailFactory {
    private final Map<EmailType,MailSend<?>> mailMap=new HashMap<>();


    //mailsendList는 mailsend라는 타입을 가지는 컴포넌트들을 모아서 di해준다 알아서.
    public MailFactory(List<MailSend<?>> mailSendList) {
        for(MailSend<?> mailSend:mailSendList){
            mailMap.put(mailSend.checkType(),mailSend);
        }
    }
    public <T> MailSend<T> supplyMailSend(EmailType emailType,Class<T> clazz){
        if(emailType==EmailType.CERTIFICATION){
            log.info("적당한 전략 전달");
            return (MailSend<T>) mailMap.get(emailType);
        }
        return null;
    }

}
