package com.org.server.certification;

import com.org.server.Mail.EmailType;
import com.org.server.Mail.MailFactory;
import com.org.server.Mail.MailSend;
import com.org.server.support.IntegralTestEnv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.when;
import com.org.server.member.domain.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.*;
public class CertificationTest extends IntegralTestEnv {


    @MockitoBean
    MailFactory mailFactory;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("인증 코드 생성시에 각 메서드가 잘호출이되는가")
    void testingCertificationCreateCall(){
        String email="Zxczx";
        MailSend<String> mailSend=Mockito.mock(MailSend.class);
        when(mailFactory.supplyMailSend(EmailType.CERTIFICATION,String.class))
                        .thenReturn(mailSend);
        Mockito.doNothing()
                .when(redisUserInfoService)
                .setCertCode(Mockito.any(String.class), Mockito.any(String.class));
        Mockito.doNothing()
                .when(mailSend)
                .sendMail(Mockito.any(String.class), Mockito.any(String.class));
        certificationService.createCertCode(email);
        Mockito.verify(redisUserInfoService).setCertCode(Mockito.any(String.class),Mockito.any(String.class));
        Mockito.verify(mailSend).sendMail(Mockito.any(String.class),Mockito.any(String.class));
    }

    @Test
    @DisplayName("인증 코드 확인시 호출 체크")
    void testingCertificationCheckCall(){
        String email="Zxczx";
        String code="Zxczxcx";

        Mockito.when(redisUserInfoService.checkCertCode(email,
                        code))
                .thenReturn(true);
        certificationService.checkCode(email,code);
        Mockito.verify(redisUserInfoService).checkCertCode(email,code);
    }

    @Test
    @DisplayName("임시 비밀번호 발급확인")
    void testingPasswordCreate(){

        Member m=createMember(1L);

        String email=m.getEmail();
        MailSend<String> mailSend=Mockito.mock(MailSend.class);
        when(mailFactory.supplyMailSend(EmailType.PASSWORD,String.class))
                .thenReturn(mailSend);
        Mockito.doNothing()
                .when(mailSend)
                .sendMail(Mockito.any(String.class), Mockito.any(String.class));
        certificationService.createNewPassword(email);
        Mockito.verify(mailSend).sendMail(Mockito.any(String.class),Mockito.any(String.class));

        Member m2=memberRepository.findByEmail(m.getEmail()).get();

        assertThat(passwordEncoder.matches(m.getPassword(),m2.getPassword())).isFalse();

    }

}
