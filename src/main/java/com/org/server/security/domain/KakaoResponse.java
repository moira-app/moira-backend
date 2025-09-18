package com.org.server.security.domain;

import com.org.server.member.GenderType;
import com.org.server.member.MemberType;

import java.util.Collections;
import java.util.Map;

import static com.org.server.member.MemberType.KAKAO;

public class KakaoResponse implements OAuth2Response {
    private final Map<String,Object> attributes;
    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    private Map<String,Object> KakaoAccount(Map<String,Object> attr){
        return (Map<String, Object>) attr.getOrDefault("kakao_account", Collections.emptyMap());
    }
    @Override
    public MemberType getProvider() {
        return KAKAO;
    }
    @Override
    public String getEmail() {
        return (String) KakaoAccount(attributes).get("email");
    }


}
