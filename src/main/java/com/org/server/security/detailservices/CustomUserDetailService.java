package com.org.server.security.detailservices;

import com.org.server.member.MemberType;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.security.domain.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;


@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private  SecurityMemberReadService service;

    public CustomUserDetailService(SecurityMemberReadService service) {
        this.service=service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member=service.findMemberCheckDelAndNull(username);

        if(member.getMemberType()!= MemberType.LOCAL){
            log.info("{}는 일반 회원이 아닙니다",username);
            throw new RuntimeException("일반 회원이 아닙니다.");
        }
        log.info("로컬 유저-{} 로그인 시도중",member.getEmail());
        return new CustomUserDetail(member);
    }
}
