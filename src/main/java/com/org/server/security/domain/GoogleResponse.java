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
    public String getBirthday() {
        return null;
    }

    @Override
    public String getBirthYear() {
        return null;
    }

    @Override
    public String getBirthMonth() {
        return null;
    }

    @Override
    public GenderType getGender() {
        return null;
    }
}
