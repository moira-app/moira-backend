package com.org.server.security.domain;

import com.org.server.member.GenderType;
import com.org.server.member.MemberType;
import org.antlr.v4.runtime.atn.SemanticContext;

import javax.print.attribute.standard.MediaSize;
import java.util.Collections;
import java.util.Map;

import static com.org.server.member.MemberType.NAVER;

public class NaverResponse implements OAuth2Response{

    private final Map<String,Object> attributes;
    public NaverResponse(Map<String,Object> attributes) {
        this.attributes=attributes;
    }
    private Map<String,Object> getAttrs(){
        return (Map<String, Object>)
                attributes.getOrDefault("response", Collections.emptyMap());
    }
    @Override
    public MemberType getProvider() {
        return NAVER;
    }
    @Override
    public String getEmail() {
        return (String) getAttrs().get("email");
    }
    @Override
    public String getBirthday() {

        String birth=(String) getAttrs().get("birthday");
        return birth.split("-")[1];
    }
    @Override
    public String getBirthYear() {
        return (String) getAttrs().get("year");
    }
    @Override
    public String getBirthMonth() {
        String birth=(String) getAttrs().get("birthday");
        return birth.split("-")[0];
    }
    @Override
    public GenderType getGender() {
        String gender=(String) getAttrs().get("gender");
        return gender.equals("F") ? GenderType.Female :
                gender.equals("U") ? null:GenderType.Male;
    }
}
