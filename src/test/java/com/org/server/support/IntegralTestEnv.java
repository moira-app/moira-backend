package com.org.server.support;


import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class IntegralTestEnv {

    @Autowired
    protected MemberRepository memberRepository;


    @AfterEach
    void deleteAll(){
        memberRepository.deleteAllInBatch();
    }



    protected Member createMember(Long idx){

        Member member=Member.builder()
                .email("test@"+idx+"test.com")
                .nickName("test"+idx)
                .password("1234")
                .build();
        member=memberRepository.save(member);
        return member;
    }
}
