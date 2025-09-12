package com.org.server.support;


import com.org.server.member.MemberType;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.member.service.MemberServiceImpl;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.util.jwt.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

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
    protected JwtUtil jwtUtil;


    @Autowired
    protected PasswordEncoder passwordEncoder;
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
