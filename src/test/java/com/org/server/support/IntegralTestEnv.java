package com.org.server.support;


import com.org.server.certification.CertificationTest;
import com.org.server.certification.service.CertificationService;
import com.org.server.member.MemberType;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.member.service.MemberServiceImpl;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.util.jwt.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ActiveProfiles("test")
@SpringBootTest
public class IntegralTestEnv {


    //repository
    @Autowired
    protected MemberRepository memberRepository;


    //service

    @Autowired
    protected MemberServiceImpl memberService;
    @Autowired
    protected SecurityMemberReadService securityMemberReadService;

    @Autowired
    protected CertificationService certificationService;
    @MockitoBean
    protected RedisUserInfoService redisUserInfoService;

    @Autowired
    protected JwtUtil jwtUtil;

    //else
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected JavaMailSender javaMailSender;
    @MockitoBean
    protected SpringTemplateEngine springTemplateEngine;
    @AfterEach
    void deleteAll(){
        memberRepository.deleteAllInBatch();
    }



    protected Member createMember(Long idx){

        Member member=Member.builder()
                .email("test@"+idx+"test.com")
                .nickName("test"+idx)
                .password(passwordEncoder.encode("1234"))
                .memberType(MemberType.LOCAL)
                .build();
        member=memberRepository.save(member);
        return member;
    }
}
