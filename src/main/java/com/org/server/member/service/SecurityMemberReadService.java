package com.org.server.member.service;

import com.org.server.exception.MoiraException;
import com.org.server.member.domain.Member;
import com.org.server.security.domain.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.org.server.member.repository.MemberRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityMemberReadService {

    private final MemberRepository memberRepository;

    public Member securityMemberRead(){
        CustomUserDetail customUserDetail=
                (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
        return customUserDetail.getMember();
    }

    public Member findMemberCheckDelAndNull(Long memberId){
        Optional<Member> member=memberRepository.findById(memberId);
        if(member.isEmpty()||member.get().getDeleted()){
            throw new MoiraException("없는 회원 입니다", HttpStatus.BAD_REQUEST);
        }
        return member.get();
    }

    public Member findMemberCheckDelAndNull(String email){
        Optional<Member> member=memberRepository.findByEmail(email);
        if(member.isEmpty()||member.get().getDeleted()){
            throw new MoiraException("없는 회원 입니다", HttpStatus.BAD_REQUEST);
        }
        return member.get();
    }

}
