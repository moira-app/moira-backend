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

}
