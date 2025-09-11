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
    @Override
    public String getBirthday() {
        String brith=(String)KakaoAccount(attributes).get("birthday");
        return brith.substring(2,3);
    }
    @Override
    public String getBirthYear() {
        return (String)KakaoAccount(attributes).get("birthyear");
    }
    @Override
    public String getBirthMonth() {
        String brith=(String)KakaoAccount(attributes).get("birthday");
        return brith.substring(0,1);
    }
    @Override
    public GenderType getGender() {
        return (GenderType) KakaoAccount(attributes).get("gender");
    }



}
