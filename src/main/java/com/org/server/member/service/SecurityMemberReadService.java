package com.org.server.member.service;

import com.org.server.member.domain.Member;
import com.org.server.security.domain.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityMemberReadService {

    public Member securityMemberRead(){
        CustomUserDetail customUserDetail=
                (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication()
                        .getPrincipal();
        return customUserDetail.getMember();
    }
}
