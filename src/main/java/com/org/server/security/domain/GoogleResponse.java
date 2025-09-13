package com.org.server.security.domain;

import com.org.server.member.GenderType;
import com.org.server.member.MemberType;

import java.util.Map;

public class GoogleResponse implements OAuth2Response{

    private Map<String,Object> attrs;

    public GoogleResponse(Map<String, Object> attrs) {
        this.attrs = attrs;
    }

    @Override
    public MemberType getProvider() {
        return MemberType.GOOGLE;
    }

    @Override
    public String getEmail() {
        return (String) attrs.get("email");
    }

    @Override
    public Integer getBirthday() {
        return null;
    }

    @Override
    public Integer getBirthYear() {
        return null;
    }

    @Override
    public Integer getBirthMonth() {
        return null;
    }

    @Override
    public GenderType getGender() {
        return null;
    }
}
