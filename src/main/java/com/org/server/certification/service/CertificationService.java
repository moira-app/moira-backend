package com.org.server.certification.service;


import com.org.server.Mail.EmailType;
import com.org.server.Mail.MailFactory;
import com.org.server.Mail.MailSend;
import com.org.server.exception.MoiraException;
import com.org.server.redis.service.RedisIntegralService;
import com.org.server.util.RandomCharSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificationService {


    private final MailFactory mailFactory;
    private final RedisIntegralService redisUserInfoService;
    public void createCertCode(String email){
        MailSend<String> mailSend=mailFactory.supplyMailSend(EmailType.CERTIFICATION, String.class);
        String code= RandomCharSet.createRandomName();
        redisUserInfoService.setCertCode(email,code);
        mailSend.sendMail(email,code);
    }

    public void checkCode(String email,String code){
        if(redisUserInfoService.checkCertCode(email,code)){
            return ;
        }
        throw new MoiraException("인증코드가 일치하지않습니다",HttpStatus.BAD_REQUEST);
    }
}
