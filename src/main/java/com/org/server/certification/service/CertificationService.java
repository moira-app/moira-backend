package com.org.server.certification.service;


import com.org.server.Mail.EmailType;
import com.org.server.Mail.MailFactory;
import com.org.server.Mail.MailSend;
import com.org.server.exception.MoiraException;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.util.RandomCharSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.org.server.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import com.org.server.member.domain.Member;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;


@Service
@RequiredArgsConstructor
@Transactional
public class CertificationService {


    private final MemberRepository memberRepository;
    private final MailFactory mailFactory;
    private final RedisUserInfoService redisUserInfoService;
    private final PasswordEncoder passwordEncoder;

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

    public void createNewPassword(String email){

        Optional<Member> member=memberRepository.findByEmail(email);
        if(member.isEmpty()){
            throw new MoiraException("없는 회원입니다",HttpStatus.BAD_REQUEST);
        }
        MailSend<String> mailSend=mailFactory.supplyMailSend(EmailType.PASSWORD,String.class);

        String password=RandomCharSet.createRandomName();
        member.get().updatePassword(passwordEncoder.encode(password));
        mailSend.sendMail(email,password);

    }

}
