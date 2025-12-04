package com.org.server.security.domain;

import com.org.server.member.MemberType;
import com.org.server.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Getter
public class CustomOAuth2User implements OAuth2User{
    private Long id;
    private String memberRole;
    private MemberType memberType;
    private String email;
    private Member member;

    @Builder
    public CustomOAuth2User(Long id, String memberRole,
                            MemberType memberType, String email,Member member) {
        this.id = id;
        this.memberRole = memberRole;
        this.memberType = memberType;
        this.email = email;
        this.member=member;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection=new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return memberRole;
            }
        });

        return collection;
    }


    //이거 안쓰긴하는대 뭔가값이 들어있긴 해야되나보다.
    @Override
    public String getName() {
        return "dont use";
    }
}
